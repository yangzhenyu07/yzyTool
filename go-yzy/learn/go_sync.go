package learn

// 简单并发互斥场景
// sync.Mutex 适合简单互斥场景，如保护共享变量的访问
import (
	"fmt"
	"runtime"
	"sync"
	"time"
)

var counter int
var mu sync.Mutex // 互斥锁：保护 counter

// 获取当前 goroutine 的唯一 ID
func getGoroutineID() uint64 {
	var buf [64]byte
	n := runtime.Stack(buf[:], false)
	var id uint64
	fmt.Sscanf(string(buf[:n]), "goroutine %d", &id)
	return id
}

func goTest() {
	var wg sync.WaitGroup
	wg.Add(2)

	go func() {
		// 获取当前 goroutine 的 ID
		goroutineID := getGoroutineID()
		count(5, "yangzhenyu", goroutineID)
		// 在 goroutine 中调用 wg.Done() 来通知 WaitGroup 任务完成
		defer wg.Done()
	}()

	go func() {
		goroutineID := getGoroutineID()
		count(5, "liuyue", goroutineID)
		defer wg.Done()
	}()
	// 等待两个 goroutine 完成
	wg.Wait()
	fmt.Println("done")
	// 打印最终的 counter 值
	fmt.Printf("Final counter value: %d\n", counter)
}

// count 函数执行计数，并打印当前 goroutine 的 ID 和计数值
func count(n int, context string, goroutineID uint64) {
	for i := 0; i < n; i++ {
		// 打印当前 goroutine 的 ID、上下文信息和计数值
		fmt.Printf("Goroutine %d: %s %d\n", goroutineID, context, i+1)
		time.Sleep(500 * time.Millisecond)

		// 加锁：同一时间只有一个 goroutine 修改 counter
		mu.Lock()
		counter++
		// 解锁：允许其他 goroutine 修改 counter
		mu.Unlock()
	}
}
