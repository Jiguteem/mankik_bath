const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  transpileDependencies: true
},
{
  //api 서버 설정 http://3.36.85.108:8080/
    proxy: {
      '/api': {
        target: 'http://3.38.251.7:8080/',
        changeOrigin: true,
        pathRewrite: {
          '^/api': ''
        }
      }
    },
  outputDir: '../backend/public',
}
)
