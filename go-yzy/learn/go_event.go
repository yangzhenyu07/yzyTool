package learn

// 事件监听器
import (
	"context"
	"fmt"
	"sync"
	"time"
)

// 统一日志工具
func log(format string, args ...interface{}) {
	now := time.Now().Format("2006-01-02 15:04:05")
	fmt.Printf("[%s] %s\n", now, fmt.Sprintf(format, args...))
}

// PersistentEvent 持久事件
type PersistentEvent struct {
	ctx     context.Context    // 上下文，用于取消事件
	cancel  context.CancelFunc // 取消函数，用于触发主循环关闭
	wg      sync.WaitGroup     // 等待组，用于跟踪所有任务完成
	ticker  *time.Ticker       // 定时器，每15秒触发一次
	running bool               // 运行状态标志，保护访问
	mu      sync.Mutex         // 互斥锁，用于保护运行状态标志
}

// 全局单例（保证整个程序只有一个实例）
var (
	eventInstance *PersistentEvent // 全局唯一实例
	eventOnce     sync.Once        // 用于确保只初始化一次
)

// GetEventInstance 获取全局唯一实例（生产标准写法）
func GetEventInstance() *PersistentEvent {
	eventOnce.Do(func() {
		ctx, cancel := context.WithCancel(context.Background())
		eventInstance = &PersistentEvent{
			ctx:    ctx,
			cancel: cancel,
			ticker: time.NewTicker(5 * time.Second),
		}
	})
	return eventInstance // 返回全局唯一实例
}

// Start 启动事件
func (e *PersistentEvent) Start() {
	e.mu.Lock()
	if e.running {
		e.mu.Unlock()
		log("事件已经在运行中，无需重复启动")
		return
	}
	e.running = true
	e.mu.Unlock()

	log("监听事件已启动，每5秒生成一个任务")

	for {
		select {
		case <-e.ticker.C:
			e.wg.Add(1)    // 增加等待组计数器
			go e.runTask() // 启动任务执行协程

		case <-e.ctx.Done():
			log("停止信号已接收，主循环关闭...")
			e.ticker.Stop() // 停止定时器

			e.mu.Lock()
			e.running = false
			e.mu.Unlock()
			return
		}
	}
}

// runTask 任务执行
func (e *PersistentEvent) runTask() {
	defer e.wg.Done() // 任务完成后减少等待组计数器
	taskID := time.Now().Unix()

	log("任务 [%d] 开始执行", taskID)
	time.Sleep(2 * time.Second)
	log("任务 [%d] 执行完成", taskID)
}

// Stop 优雅停止
func (e *PersistentEvent) Stop() {
	e.mu.Lock()
	if !e.running {
		e.mu.Unlock()
		log("事件已经停止，无需重复停止")
		return
	}
	e.mu.Unlock()

	e.cancel()  // 触发主循环关闭
	e.wg.Wait() // 等待所有任务完成
	log("所有任务已完成，事件完全停止")
}

// IsRunning 安全获取状态
func (e *PersistentEvent) IsRunning() bool {
	e.mu.Lock()
	defer e.mu.Unlock()
	return e.running // 返回当前运行状态标志
}

// goEvent 测试/启动入口
func goEvent() {
	// 全局单例获取，永远是同一个
	event := GetEventInstance()

	// 后台启动
	go event.Start()

	// 等待启动成功
	for !event.IsRunning() {
		time.Sleep(1 * time.Millisecond)
	}

	// 25秒后停止
	time.AfterFunc(25*time.Second, func() {
		log("模拟25秒后外部调用停止接口...")
		event.Stop()
	})

	// 等待结束
	for event.IsRunning() {
		time.Sleep(1 * time.Second)
	}

	log("程序正常退出")
}
