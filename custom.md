### 登录仓库

> 注意：只需要登录一次
> 密码：************

```bash
docker login --username=<账号> registry.cn-chengdu.aliyuncs.com
```

### docker打包

```bash
docker buildx build . -f custom.dockerfile -t registry.cn-chengdu.aliyuncs.com/hlzt/wvp-pro:<版本号> --progress=plain
# 如:
docker buildx build . -f custom.dockerfile -t registry.cn-chengdu.aliyuncs.com/hlzt/wvp-pro:20240228172202 --progress=plain
```

### 推送镜像

```bash
docker push registry.cn-chengdu.aliyuncs.com/hlzt/wvp-pro:<版本号>
# 如:
docker push registry.cn-chengdu.aliyuncs.com/hlzt/wvp-pro:20240228172202
```

### 保存离线包

```bash
docker save registry.cn-chengdu.aliyuncs.com/hlzt/wvp-pro:<版本号> > registry.cn-chengdu.aliyuncs.com#hlzt#wvp-pro^<版本号>.tar
# 如:
docker save registry.cn-chengdu.aliyuncs.com/hlzt/wvp-pro:20240228172202 > registry.cn-chengdu.aliyuncs.com#hlzt#wvp-pro^20240228172202.tar
```

### 加载离线包

```bash
docker load < registry.cn-chengdu.aliyuncs.com#hlzt#wvp-pro^<版本号>.tar
# 如:
docker load < registry.cn-chengdu.aliyuncs.com#hlzt#wvp-pro^20240228172202.tar
```