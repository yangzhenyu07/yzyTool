package logger

import (
	"context"
	"os"
	"path/filepath"
	"time"

	"yangzhenyu.com/go-yzy/internal/config"

	"go.uber.org/zap"
	"go.uber.org/zap/zapcore"
	"gopkg.in/natefinch/lumberjack.v2"
)

type contextKey string

const RequestIDKey contextKey = "request_id"

// loggerKey 用于在 context 中缓存已绑定 request_id 的 SugaredLogger，
// 避免每次 WithCtx 调用都创建新的 logger 实例。
const loggerKey contextKey = "ctx_logger"

var (
	// Log 是全局 SugaredLogger，方便开发使用
	Log *zap.SugaredLogger
	// RawLog 是全局 zap.Logger，高性能场景使用
	RawLog *zap.Logger
)

// init 包初始化函数
// 程序启动先内置一个生产级兜底日志，防止配置未初始化时无日志可用
func init() {
	RawLog = zap.Must(zap.NewProduction())
	Log = RawLog.Sugar()
}

// ===================== 日志核心初始化 =====================

// Init 初始化全局日志组件
// 根据配置：日志级别、运行模式(dev/prod)、文件切割、分级落盘、控制台彩色输出
func Init(cfg *config.LogConfig) {
	// 编码器配置
	encoderCfg := zap.NewProductionEncoderConfig()
	encoderCfg.TimeKey = "ts"
	encoderCfg.EncodeTime = zapcore.ISO8601TimeEncoder
	encoderCfg.EncodeLevel = zapcore.CapitalLevelEncoder

	// 日志级别
	level := zapcore.DebugLevel
	switch cfg.Level {
	case "debug":
		level = zapcore.DebugLevel
	case "info":
		level = zapcore.InfoLevel
	case "warn":
		level = zapcore.WarnLevel
	case "error":
		level = zapcore.ErrorLevel
	default:
		// 非法级别默认降级为info
		level = zapcore.InfoLevel
	}
	// 日志切割参数兜底默认值
	maxSize := cfg.MaxSize
	if maxSize == 0 {
		maxSize = 100 // 默认单个日志文件最大100MB
	}
	maxBackups := cfg.MaxBackups
	if maxBackups == 0 {
		maxBackups = 7 // 默认保留7个历史文件
	}
	maxAge := cfg.MaxAge
	if maxAge == 0 {
		maxAge = 30 // 默认日志保留30天
	}

	// 构建文件写入器（日志切割）
	newFileWriter := func(filename string) zapcore.WriteSyncer {
		return zapcore.AddSync(&lumberjack.Logger{
			Filename:   filepath.Join(cfg.LogDir, filename), // 拼接日志文件路径
			MaxSize:    maxSize,                             // 单文件大小限制 MB
			MaxBackups: maxBackups,                          // 保留旧文件个数
			MaxAge:     maxAge,                              // 日志保留天数
			Compress:   cfg.Compress,                        // 是否压缩归档旧日志
		})
	}

	// 带缓冲的文件写入器
	// 缓冲区256KB、每秒强制刷盘，兼顾性能与日志不丢失
	newBufferedFileWriter := func(filename string) zapcore.WriteSyncer {
		return &zapcore.BufferedWriteSyncer{
			WS:            newFileWriter(filename),
			Size:          256 * 1024,  // 缓冲区大小 256KB
			FlushInterval: time.Second, // 定时1秒强制刷新缓冲区
		}
	}

	var cores []zapcore.Core

	// 控制台输出：所有级别
	// 控制台输出
	if cfg.Mode == "dev" {
		// 开发环境：控制台彩色、友好格式
		devCfg := encoderCfg
		devCfg.EncodeLevel = zapcore.CapitalColorLevelEncoder
		cores = append(cores, zapcore.NewCore(
			zapcore.NewConsoleEncoder(devCfg),
			zapcore.AddSync(os.Stdout),
			level,
		))
	} else {
		// 生产环境：控制台输出JSON结构化日志
		cores = append(cores, zapcore.NewCore(
			zapcore.NewJSONEncoder(encoderCfg),
			zapcore.AddSync(os.Stdout),
			level,
		))
	}
	// 配置目录不为空时，开启文件分级落盘
	if cfg.LogDir != "" {
		// app.log：输出所有级别日志
		cores = append(cores, zapcore.NewCore(
			zapcore.NewJSONEncoder(encoderCfg),
			newBufferedFileWriter("app.log"),
			level,
		))

		// warn.log：只单独记录Warn级别
		cores = append(cores, zapcore.NewCore(
			zapcore.NewJSONEncoder(encoderCfg),
			newBufferedFileWriter("warn.log"),
			zap.LevelEnablerFunc(func(l zapcore.Level) bool {
				return l == zapcore.WarnLevel
			}),
		))

		// error.log：记录Error及Panic/Fatal级别
		cores = append(cores, zapcore.NewCore(
			zapcore.NewJSONEncoder(encoderCfg),
			newBufferedFileWriter("error.log"),
			zap.LevelEnablerFunc(func(l zapcore.Level) bool {
				return l >= zapcore.ErrorLevel
			}),
		))
	}

	// 合并多输出核心、创建日志实例
	// AddCaller：打印代码行号；AddCallerSkip(1)：跳过封装层，定位真实业务行
	zapLog := zap.New(
		zapcore.NewTee(cores...),
		zap.AddCaller(),
		zap.AddCallerSkip(1),
	)

	// 赋值全局日志变量
	RawLog = zapLog
	Log = zapLog.Sugar()
}

// WithCtx 从上下文获取带request_id的日志实例
// 优先复用ctx中缓存的logger，避免重复创建对象
func WithCtx(ctx context.Context) *zap.SugaredLogger {
	// 日志未初始化时返回空日志，不panic
	if RawLog == nil {
		return zap.NewNop().Sugar()
	}
	// 空上下文返回全局默认日志
	if ctx == nil {
		return Log
	}

	// 优先取上下文缓存的logger实例
	if cached, ok := ctx.Value(loggerKey).(*zap.SugaredLogger); ok && cached != nil {
		return cached
	}

	// 提取request_id，生成带链路ID的日志
	if reqID, ok := ctx.Value(RequestIDKey).(string); ok && reqID != "" {
		return RawLog.With(zap.String("request_id", reqID)).Sugar()
	}

	// 无request_id返回全局默认日志
	return Log
}

// NewContext 封装request_id到上下文，并缓存带链路ID的logger
// 供后续WithCtx快速复用，减少高并发内存分配
func NewContext(ctx context.Context, reqID string) context.Context {
	// 先把request_id存入上下文
	ctx = context.WithValue(ctx, RequestIDKey, reqID)

	// 生成带request_id的logger并缓存到上下文
	if RawLog != nil && reqID != "" {
		l := RawLog.With(zap.String("request_id", reqID)).Sugar()
		ctx = context.WithValue(ctx, loggerKey, l)
	}
	return ctx
}

// ===================== 工具方法 =====================

// Sync 强制刷新日志缓冲区
// 优雅停机时必须调用，防止缓冲区日志丢失
func Sync() {
	if Log != nil {
		_ = Log.Sync()
	}
}
