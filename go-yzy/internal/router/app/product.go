package app

import (
	"yangzhenyu.com/go-yzy/internal/handler"

	"github.com/gin-gonic/gin"
)

func RegisterProductRoutes(rg *gin.RouterGroup, h *handler.ProductHandler) {
	rg.GET("/products", h.List)
	rg.GET("/products/:id", h.Get)
}
