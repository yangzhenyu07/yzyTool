package learn

import "testing"

func Test_goEvent(t *testing.T) {
	tests := []struct {
		name string
	}{
		// TODO: Add test cases.
		{name: "run my test"},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			goEvent()
		})
	}
}
