package middleware

import (
	"runtime/debug"

	"yangzhenyu.com/go-yzy/internal/pkg/errcode"
	"yangzhenyu.com/go-yzy/internal/pkg/logger"
	"yangzhenyu.com/go-yzy/internal/pkg/response"

	"github.com/gin-gonic/gin"
)

func Recovery() gin.HandlerFunc {
	return func(c *gin.Context) {
		defer func() {
			if err := recover(); err != nil {
				logger.WithCtx(c.Request.Context()).Errorw("panic recovered",
					"error", err,
					"stack", string(debug.Stack()),
				)
				response.Error(c, errcode.ErrInternal())
				c.Abort()
			}
		}()
		c.Next()
	}
}
