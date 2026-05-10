package learn

import "testing"

func Test_goTest(t *testing.T) {
	tests := []struct {
		name string
	}{
		// TODO: Add test cases.
		{name: "run my test"},
	}
	// 这里我们使用 t.Run 来运行子测试，每个子测试调用 goTest 函数
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			goTest()
		})
	}
}
