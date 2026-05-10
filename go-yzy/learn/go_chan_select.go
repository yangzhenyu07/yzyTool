package learn

// 任务分发模型场景
// 引入了一个中央调度器（waitForWorkers），
// 它通过 select 来监听三个通道：searchRequest、workerDone 和 foundMatch。
// 这样我们就不需要在每个 worker 中直接修改 matches 变量，而是通过 foundMatch 通道来通知中央调度器更新计数。
// 同时，workerDone 通道用于通知中央调度器某个 worker 已经完成任务，从而进行计数和退出控制。
import (
	"fmt"
	"os"
	"path/filepath"
	"sync/atomic"
	"time"
)

var query = "index"
var matches int64

// 任务分发 + 计数 + 退出控制
var workerCount atomic.Int32

// 最大 worker 数量，避免过多 goroutine 导致资源耗尽
var maxWorkerCount = 32

// 任务分发通道（加缓冲，避免死锁）
var searchRequest = make(chan string, 1000)

// worker 完成信号通道（加缓冲，避免死锁）
var workerDone = make(chan struct{}, 1000)

// 匹配信号通道（加缓冲，避免死锁）
var foundMatch = make(chan struct{}, 1000)

func goChanSelect() {
	start := time.Now()
	matches = 0
	workerCount.Store(1)

	// 启动主搜索
	go search("E:\\web3\\")

	// 中央调度
	waitForWorkers()

	fmt.Println("匹配数量：", matches)
	fmt.Println("耗时：", time.Since(start))
}

// 中央调度：任务分发 + 计数 + 退出控制
func waitForWorkers() {
	for {
		select {
		case path := <-searchRequest:
			// 收到新的搜索请求，启动新的 worker
			workerCount.Add(1)
			go search(path)

		case <-workerDone:
			// 收到 worker 完成信号，减少 worker 数量
			workerCount.Add(-1)
			if workerCount.Load() == 0 {
				fmt.Println("✅ 所有工作完成")
				return
			}

		case <-foundMatch:
			// 收到匹配信号，增加匹配计数
			atomic.AddInt64(&matches, 1)
		}
	}
}

// 搜索逻辑（去掉 master，统一结构）
func search(path string) {
	// 在函数退出时发送完成信号，通知中央调度器
	defer func() { workerDone <- struct{}{} }()

	files, err := os.ReadDir(path)
	if err != nil {
		// fmt.Println("读取失败:", err)
		return
	}

	for _, file := range files {
		if file.Name() == query {
			// 找到匹配文件，发送匹配信号
			foundMatch <- struct{}{}
		}

		if file.IsDir() {
			fullPath := filepath.Join(path, file.Name())

			// 根据当前 worker 数量决定是直接开协程还是通过调度器分发任务
			if workerCount.Load() < int32(maxWorkerCount) {
				// 交给中央调度器处理，避免过多 goroutine 导致资源耗尽
				searchRequest <- fullPath
			} else {
				// 直接开协程，不阻塞
				go search(fullPath)
			}
		}
	}
}
