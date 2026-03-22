# Stable Diffusion 图片生成脚本
# 运行方法: python sd_gen.py

import requests
import base64
import os
from datetime import datetime

# API 地址
API_URL = "http://127.0.0.1:7860"

def generate_image(prompt, negative_prompt="", steps=20, width=512, height=768):
    """调用 SD WebUI API 生成图片"""
    
    payload = {
        "prompt": prompt,
        "negative_prompt": negative_prompt,
        "steps": steps,
        "width": width,
        "height": height,
        "cfg_scale": 7,
        "sampler_index": "Euler"
    }
    
    print(f"正在生成图片: {prompt[:30]}...")
    
    response = requests.post(f"{API_URL}/sdapi/v1/txt2img", json=payload)
    
    if response.status_code == 200:
        result = response.json()
        # 提取 base64 图片数据
        image_data = result['images'][0]
        
        # 生成文件名
        timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
        filename = f"sd_image_{timestamp}.png"
        
        # 保存图片
        image_bytes = base64.b64decode(image_data)
        with open(filename, 'wb') as f:
            f.write(image_bytes)
        
        print(f"✅ 图片已保存: {filename}")
        return filename
    else:
        print(f"❌ 生成失败: {response.status_code}")
        return None

if __name__ == "__main__":
    # 示例提示词
    example_prompts = [
        "1girl, beautiful, anime style, detailed eyes, morning light",
        "landscape, mountains, sunrise, mist, digital painting",
        "cyberpunk city, neon lights, rain, futuristic"
    ]
    
    print("=" * 50)
    print("Stable Diffusion 图片生成器")
    print("=" * 50)
    print("\n示例提示词:")
    for i, p in enumerate(example_prompts, 1):
        print(f"{i}. {p}")
    
    print("\n请输入提示词 (直接回车使用示例1):")
    prompt = input("> ").strip()
    
    if not prompt:
        prompt = example_prompts[0]
    
    negative = input("负面提示词 (直接回车跳过): ").strip()
    
    generate_image(prompt, negative)
