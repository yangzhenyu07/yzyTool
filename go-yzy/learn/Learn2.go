package learn

import "fmt"

func testVar() {
	x := 5          // 普通变量
	p := &x         // &x 取 x 的地址
	fmt.Println(p)  // 输出 x 的内存地址
	fmt.Println(*p) // *p 解引用 → 5

	*p = 10        // 通过指针修改 x
	fmt.Println(x) // 输出 10
}
