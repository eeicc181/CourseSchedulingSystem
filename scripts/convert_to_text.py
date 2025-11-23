#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
将项目中的所有源代码文件转换为纯文本格式，保存到docs目录
"""

import os
import shutil
from pathlib import Path

# 项目根目录
PROJECT_ROOT = Path(r"D:\AAA_CodeLearning\CourseArrange")
# 输出目录
OUTPUT_DIR = PROJECT_ROOT / "docs" / "source_texts"

# 需要排除的目录
EXCLUDE_DIRS = {
    'node_modules', 'target', '.idea', '.metals', '.vscode', 
    'logs', 'npm-cache', 'docs', '__pycache__', '.git'
}

# 需要排除的文件扩展名
EXCLUDE_EXTENSIONS = {
    '.class', '.jar', '.zip', '.7z', '.png', '.jpg', '.jpeg', 
    '.gif', '.ico', '.eot', '.ttf', '.woff', '.woff2', '.svg',
    '.pyc', '.pyo', '.exe', '.dll', '.so', '.dylib'
}

# 需要转换的文件扩展名（文本文件）
TEXT_EXTENSIONS = {
    '.java', '.xml', '.yml', '.yaml', '.properties', '.sql',
    '.js', '.vue', '.html', '.css', '.json', '.md',
    '.txt', '.sh', '.bat', '.py'
}


def should_process_file(file_path):
    """判断文件是否需要处理"""
    # 检查文件扩展名
    if file_path.suffix.lower() in EXCLUDE_EXTENSIONS:
        return False
    
    # 如果不在文本扩展名列表中，也跳过
    if file_path.suffix.lower() not in TEXT_EXTENSIONS:
        return False
    
    return True


def should_skip_directory(dir_path):
    """判断目录是否需要跳过"""
    dir_name = dir_path.name
    return dir_name in EXCLUDE_DIRS


def collect_files(root_dir):
    """收集所有需要转换的文件"""
    files_to_process = []
    
    for root, dirs, files in os.walk(root_dir):
        root_path = Path(root)
        
        # 过滤掉需要排除的目录
        dirs[:] = [d for d in dirs if not should_skip_directory(root_path / d)]
        
        for file in files:
            file_path = root_path / file
            if should_process_file(file_path):
                files_to_process.append(file_path)
    
    return files_to_process


def convert_file_to_text(source_file, output_dir, project_root):
    """将单个文件转换为文本格式"""
    try:
        # 计算相对路径
        rel_path = source_file.relative_to(project_root)
        
        # 创建输出文件路径（保持目录结构）
        output_file = output_dir / rel_path
        output_file = output_file.with_suffix(output_file.suffix + '.txt')
        
        # 创建输出目录
        output_file.parent.mkdir(parents=True, exist_ok=True)
        
        # 读取源文件并写入文本文件
        try:
            with open(source_file, 'r', encoding='utf-8') as f:
                content = f.read()
        except UnicodeDecodeError:
            # 如果UTF-8失败，尝试其他编码
            try:
                with open(source_file, 'r', encoding='gbk') as f:
                    content = f.read()
            except:
                with open(source_file, 'r', encoding='latin-1') as f:
                    content = f.read()
        
        # 写入输出文件
        with open(output_file, 'w', encoding='utf-8') as f:
            # 写入文件头信息
            f.write(f"=" * 80 + "\n")
            f.write(f"源文件: {rel_path}\n")
            f.write(f"=" * 80 + "\n\n")
            f.write(content)
        
        return True, str(rel_path)
    
    except Exception as e:
        return False, f"{rel_path}: {str(e)}"


def create_index_file(output_dir, processed_files):
    """创建索引文件"""
    index_file = output_dir / "INDEX.txt"
    
    with open(index_file, 'w', encoding='utf-8') as f:
        f.write("=" * 80 + "\n")
        f.write("项目源代码文件索引\n")
        f.write("=" * 80 + "\n\n")
        f.write(f"总文件数: {len(processed_files)}\n\n")
        
        # 按目录分组
        files_by_dir = {}
        for file_path in sorted(processed_files):
            dir_name = str(Path(file_path).parent)
            if dir_name not in files_by_dir:
                files_by_dir[dir_name] = []
            files_by_dir[dir_name].append(Path(file_path).name)
        
        for dir_name in sorted(files_by_dir.keys()):
            f.write(f"\n[{dir_name}]\n")
            for file_name in sorted(files_by_dir[dir_name]):
                f.write(f"  - {file_name}\n")


def main():
    """主函数"""
    print("=" * 80)
    print("开始转换项目文件到纯文本格式")
    print("=" * 80)
    
    # 创建输出目录
    if OUTPUT_DIR.exists():
        print(f"\n清理旧的输出目录: {OUTPUT_DIR}")
        shutil.rmtree(OUTPUT_DIR)
    
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    print(f"输出目录: {OUTPUT_DIR}\n")
    
    # 收集文件
    print("正在扫描文件...")
    files_to_process = collect_files(PROJECT_ROOT)
    print(f"找到 {len(files_to_process)} 个文件需要转换\n")
    
    # 转换文件
    processed_files = []
    failed_files = []
    
    for i, file_path in enumerate(files_to_process, 1):
        success, info = convert_file_to_text(file_path, OUTPUT_DIR, PROJECT_ROOT)
        
        if success:
            processed_files.append(info)
            print(f"[{i}/{len(files_to_process)}] ✓ {info}")
        else:
            failed_files.append(info)
            print(f"[{i}/{len(files_to_process)}] ✗ {info}")
    
    # 创建索引文件
    print("\n正在创建索引文件...")
    create_index_file(OUTPUT_DIR, processed_files)
    
    # 输出统计信息
    print("\n" + "=" * 80)
    print("转换完成!")
    print("=" * 80)
    print(f"成功: {len(processed_files)} 个文件")
    print(f"失败: {len(failed_files)} 个文件")
    print(f"输出目录: {OUTPUT_DIR}")
    
    if failed_files:
        print("\n失败的文件:")
        for failed in failed_files:
            print(f"  - {failed}")


if __name__ == "__main__":
    main()
