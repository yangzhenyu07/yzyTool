package org.example.config.web3;

import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.apache.commons.lang3.StringUtils;
import org.example.config.web3.tag.Web3NodeUtils;
import org.example.exception.ExceptionEnum;
import org.example.exception.throwtype.RunException;
import org.springframework.stereotype.Component;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Web3j多链多节点客户端管理器（Spring单例Bean + 工厂模式）
 * 通过私有线程进行动态链路/节点选择(当交易场景时为链key，当查询场景时为rpc name key)
 * 多节点路由 +  (查询类)加权轮询动态负载均衡 + (交易类)固定链固定节点模式 + RPC健康检查
 * @author 杨镇宇
 * @date 2026/3/19 22:33
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class Web3jClientFactory {

    private final Web3Properties web3Properties;
    // 交易类 key(urlName) : value(url)
    private final Map<String, String> urlMapper = new ConcurrentHashMap<>();

    // 普通客户端池（每个URL一个）
    private final Map<String, Web3j> clientPool = new ConcurrentHashMap<>();

    // 权重轮询池
    private final Map<String, List<Web3j>> weightedPool = new ConcurrentHashMap<>();

    // 轮询计数器
    private final AtomicInteger counter = new AtomicInteger(0);

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        web3Properties.getNodes().forEach((key, nodeList) -> {

            // 1、构建 Web3j 客户端
            for (Web3Properties.Web3NodeVO node : nodeList) {
                // Empty 代表着这个url暂时不配置
                if (!"Empty".equals(node.getUrl())) {
                    buildClient(node.getName(), node.getUrl());
                    urlMapper.put(node.getName(),node.getUrl());
                }
            }

            // 2、构建权重池
            buildWeightedPool(key, nodeList);
        });
    }

    /**
     * 构建单个客户端
     */
    private void buildClient(String key, String url) {
        clientPool.computeIfAbsent(key, k -> {

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(web3Properties.getConfig().getConnectTimeout(), TimeUnit.MILLISECONDS)
                    .readTimeout(web3Properties.getConfig().getReadTimeout(), TimeUnit.MILLISECONDS)
                    .build();

            return Web3j.build(new HttpService(url, client, false));
        });
    }

    /**
     * 构建权重池
     */
    private void buildWeightedPool(String chain, List<Web3Properties.Web3NodeVO> nodeList) {

        List<Web3j> list = new ArrayList<>();

        for (Web3Properties.Web3NodeVO node : nodeList) {
            // Empty 代表着这个url暂时不配置
            if (!"Empty".equals(node.getUrl())) {
                Web3j web3j = clientPool.computeIfAbsent(node.getName(), k -> buildClientInternal(node.getUrl()));
                int weight = node.getWeight() == null ? 1 : node.getWeight();
                for (int i = 0; i < weight; i++) {
                    list.add(web3j);
                }
            }
        }
        weightedPool.put(chain, list);
    }

    /**
     * 内部创建
     */
    private Web3j buildClientInternal(String url) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(web3Properties.getConfig().getConnectTimeout(), TimeUnit.MILLISECONDS)
                .readTimeout(web3Properties.getConfig().getReadTimeout(), TimeUnit.MILLISECONDS)
                .build();

        return Web3j.build(new HttpService(url, client, false));
    }

    /**
     * 获取客户端（权重轮询 + 健康检查）
     */
    public Web3j getClient() {
        String channelType = Web3NodeUtils.getChannelType();
        String channelTag = Web3NodeUtils.getChannelTag();
        String rpcName ="";
        if (StringUtils.isBlank(channelTag)) {
            throw new RunException(ExceptionEnum.ERROR_MSG, "未传入节点tag");
        }
        if ("1".equals(channelType)){
            // 交易类
            rpcName = channelTag;
            String rpcUrl = urlMapper.get(rpcName);
            if (StringUtils.isEmpty(rpcUrl)){
                throw new RunException(ExceptionEnum.ERROR_MSG, "rpcName 传入错误");
            }
            Web3j client = clientPool.get(rpcName);
            if (isHealthy(client)) {
                return client;
            }

        }else {
            // 查询类
            List<Web3j> clients = weightedPool.get(channelTag);
            if (clients == null || clients.isEmpty()) {
                throw new RunException(ExceptionEnum.ERROR_MSG, "节点未初始化");
            }

            int size = clients.size();
            for (int i = 0; i < size; i++) {
                int index = Math.abs(counter.getAndIncrement() % size);
                Web3j client = clients.get(index);
                if (isHealthy(client)) {
                    return client;
                }
            }
        }
        throw new RunException(ExceptionEnum.ERROR_MSG, "所有RPC节点不可用");
    }

    /**
     * 健康检查
     */
    private boolean isHealthy(Web3j web3j) {
        try {
            // 测试
            System.out.println(okhttp3.RequestBody.class.getProtectionDomain().getCodeSource());
            // 测试:查看具体的执行方法
            for (Method m : okhttp3.RequestBody.class.getDeclaredMethods()) {
                System.out.println(m);
            }
            web3j.netVersion().send();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 优雅关闭
     */
    @PreDestroy
    public void destroy() {
        clientPool.values().forEach(Web3j::shutdown);
        clientPool.clear();
        weightedPool.clear();
    }
}