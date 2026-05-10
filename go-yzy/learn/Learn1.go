package learn // 只有 package main 的文件才可以作为 Go 程序的入口，被 go run 或 go build 执行。

/**
常用标准库包使用
*/
import (
	"fmt"
	"io"
	"math"
	"net/http"
	"os"
	"os/user"
	"path/filepath"
	"strings"
	"time"
)

// ==================== fmt 输出 ====================
func fmtExample(name string, age int) {
	fmt.Println("Hello,", name)
	fmt.Printf("%s is %d years old\n", name, age)
}

// ==================== math 运算 ====================
func mathExample(x float64) {
	fmt.Println("平方根:", math.Sqrt(x))
	fmt.Println("圆周率:", math.Pi)
	fmt.Println("幂运算:", math.Pow(2, 3))
}

// ==================== time 时间 ====================
func timeExample(waitSeconds int) {
	now := time.Now()
	fmt.Println("当前时间:", now)
	fmt.Println("年:", now.Year())
	fmt.Println("月份:", now.Month())
	fmt.Println("格式化时间:", now.Format("2006-01-02 15:04:05"))

	fmt.Printf("等待%d秒...\n", waitSeconds)
	time.Sleep(time.Duration(waitSeconds) * time.Second)
	fmt.Println("完成")
}

// ==================== strings 字符串 ====================
func stringsExample(s string, substr string) {
	fmt.Println(strings.ToLower(s))
	fmt.Println(strings.Contains(s, substr))
	fmt.Println(strings.Split(s, " "))
}

// ==================== os 文件与环境操作 ====================
func osExample(baseDir string, filename string, content string) {

	// 获取当前用户
	u, err := user.Current()
	if err != nil {
		fmt.Println("获取当前用户失败:", err)
	} else {
		fmt.Println("当前用户:", u.Username)
	}

	// 使用传入的工作目录
	dir := baseDir
	fmt.Println("当前工作目录:", dir)

	// 构造文件路径
	absolutePath := filepath.Join(dir, "temp", filename)

	// 创建目录
	err = os.MkdirAll(filepath.Join(dir, "temp"), os.ModePerm)
	if err != nil {
		fmt.Println("创建目录失败:", err)
		return
	}

	// 创建文件
	file, err := os.Create(absolutePath)
	if err != nil {
		fmt.Println("创建文件失败:", err)
		return
	}

	// 写入内容
	_, err = file.WriteString(content)
	if err != nil {
		fmt.Println("写入文件失败:", err)
		file.Close()
		return
	}
	file.Close()
	fmt.Println("文件创建成功:", absolutePath)

	// 删除文件
	err = os.Remove(absolutePath)
	if err != nil {
		fmt.Println("删除文件失败:", err)
		return
	}
	fmt.Println("文件删除成功:", absolutePath)

	// 删除目录
	err = os.Remove(filepath.Join(dir, "temp"))
	if err != nil {
		fmt.Println("删除目录失败:", err)
		return
	}
	fmt.Println("目录删除成功: temp")
}

// ==================== net/http 简单 HTTP 请求 标准库包 ====================
// httpExample 发送 GET 请求，返回两个参数：
// 1. success：请求是否成功（状态码 200 且没有读取错误）
// 2. err：请求失败或读取失败时的错误信息
func httpExample(uri string) (success bool, err error) {
	// 发送 GET 请求
	resp, err := http.Get(uri)
	if err != nil {
		return false, fmt.Errorf("请求失败: %w", err)
	}
	defer resp.Body.Close()

	// 检查响应状态码
	if resp.StatusCode != http.StatusOK {
		return false, fmt.Errorf("HTTP 请求失败，状态码: %d", resp.StatusCode)
	}

	// 读取响应 body
	body, err := io.ReadAll(resp.Body)
	if err != nil {
		return false, fmt.Errorf("读取响应失败: %w", err)
	}

	fmt.Printf("请求 URL: %s\n响应长度: %d\n", uri, len(body))
	return true, nil // 成功返回 true
}

func mainTest() {

	name := "Alice"
	age := 25
	fmt.Println("===========================fmt 格式化输出 标准库包===============================")
	fmtExample(name, age)

	x := 16.0
	fmt.Println("===========================math 数学运算 标准库包===============================")
	mathExample(x)

	waitSeconds := 2
	fmt.Println("===========================time 时间处理 标准库包===============================")
	timeExample(waitSeconds)

	s := "Go is Awesome"
	substr := "Go"
	fmt.Println("===========================strings 字符串处理 标准库包===============================")
	stringsExample(s, substr)

	baseDir, _ := os.Getwd()                // 使用当前工作目录
	filename := "test.txt"                  // 文件名
	content := "Hello Go Standard Library!" // 文件内容
	fmt.Println("===========================os 文件和环境操作 标准库包===============================")
	osExample(baseDir, filename, content)

	fmt.Println("===========================net/http 简单 HTTP 请求 标准库包===============================")
	uri := "https://www.baidu.com"
	success, err := httpExample(uri)
	if success {
		fmt.Println("HTTP 请求成功")
	} else {
		fmt.Println("HTTP 请求失败:", err)
	}

}
