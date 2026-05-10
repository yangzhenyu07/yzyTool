package test

import (
	"os"
	"testing"

	"yangzhenyu.com/go-yzy/internal/config"
	"yangzhenyu.com/go-yzy/internal/pkg/logger"
)

func TestMain(m *testing.M) {
	logger.Init(&config.LogConfig{
		Mode:  "dev",
		Level: "debug",
	})
	os.Exit(m.Run())
}
