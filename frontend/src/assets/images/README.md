# 登录页面背景图片说明

## 当前设置
登录页面目前使用了一张来自Unsplash的沿海风景图片作为背景。

## 自定义背景图片

### 方法1：替换在线图片URL
在 `frontend/src/views/Login.vue` 文件中，找到以下CSS代码：
```css
background-image: url('https://images.unsplash.com/photo-1506905925346-21bda4d32df4?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80');
```

将URL替换为您喜欢的图片链接。

### 方法2：使用本地图片
1. 将您的图片文件放在 `frontend/src/assets/images/` 目录中
2. 在 `frontend/src/views/Login.vue` 中更新CSS：
```css
background-image: url('/src/assets/images/your-image-name.jpg');
```

## 推荐图片规格
- 分辨率：至少1920x1080像素
- 格式：JPG、PNG、WebP
- 主题：沿海风景、自然风光、城市天际线等
- 色调：建议选择较亮的图片，以确保登录框文字清晰可读

## 注意事项
- 图片文件大小建议控制在2MB以内，以确保加载速度
- 如果使用本地图片，确保图片路径正确
- 背景图片会自动适应不同屏幕尺寸 