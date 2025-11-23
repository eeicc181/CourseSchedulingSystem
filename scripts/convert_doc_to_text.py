#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
使用win32com转换.doc文件为纯文本
"""

import win32com.client
from pathlib import Path

# 需要转换的文件
DOC_FILE = r"D:\AAA_CodeLearning\CourseArrange\docs\孙传家+许东徽+柏璐瑶+梁治国+排课系统\孙传家+许东徽+柏璐瑶+梁治国+排课系统\05-《软件工程专业生产实习》答辩记录表.doc"

# 输出目录
OUTPUT_DIR = Path(r"D:\AAA_CodeLearning\CourseArrange\docs")


def convert_doc_to_text_win32(doc_path, output_dir):
    """使用win32com转换.doc文件"""
    try:
        doc_path = Path(doc_path).resolve()
        
        if not doc_path.exists():
            return False, f"文件不存在: {doc_path}"
        
        # 创建Word应用程序对象
        word = win32com.client.Dispatch("Word.Application")
        word.Visible = False
        
        # 打开文档
        doc = word.Documents.Open(str(doc_path))
        
        # 提取文本内容
        text_content = doc.Content.Text
        
        # 关闭文档
        doc.Close(False)
        word.Quit()
        
        # 生成输出文件名
        output_file = output_dir / (doc_path.stem + ".txt")
        
        # 写入文本文件
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write("=" * 80 + "\n")
            f.write(f"源文件: {doc_path.name}\n")
            f.write("=" * 80 + "\n\n")
            f.write(text_content)
        
        return True, doc_path.name
    
    except Exception as e:
        # 确保Word应用程序关闭
        try:
            word.Quit()
        except:
            pass
        return False, f"{doc_path.name}: {str(e)}"


def main():
    """主函数"""
    print("=" * 80)
    print("转换.doc文件到纯文本格式")
    print("=" * 80)
    
    success, info = convert_doc_to_text_win32(DOC_FILE, OUTPUT_DIR)
    
    if success:
        print(f"✓ 成功: {info}")
        print(f"输出目录: {OUTPUT_DIR}")
    else:
        print(f"✗ 失败: {info}")


if __name__ == "__main__":
    main()
