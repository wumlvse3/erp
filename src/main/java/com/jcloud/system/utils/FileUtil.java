package com.jcloud.system.utils;


import org.apache.poi.POIXMLDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.*;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.*;
import java.util.List;
import java.util.UUID;

/**
 * 文件操作工具类
 * @Auther: yang
 * @Date: 2023/4/4 16:08
 * @Description:
 */
public class FileUtil {

	/**
	 * 传入文件
	 * @param file  文件
	 * @param filePath 路径
	 * @param fileName 文件名字
	 * @throws Exception
	 */
	public static void uploadFile(byte[] file, String filePath, String fileName) throws Exception {
		File targetFile = new File(filePath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		FileOutputStream out = new FileOutputStream(filePath + fileName);
		out.write(file);
		out.flush();
		out.close();
	}

	/**
	 * 删除文件
	 * @param fileName 删除文件地址
	 * @return
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 获取uuid以文件名
	 * @param fileName
	 * @return
	 */
	public static String renameToUUID(String fileName) {
		return UUID.randomUUID() + "." + fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	/**
	 * 创建文件夹
	 * @param FolderName
	 */
	 public  static  void SetUpFolder(String FolderName){
		if (FolderName == "") return;
		 File folder=new File(FolderName);
		if(!folder.exists()){
			folder.mkdirs();
		}
	 }

	/**
	 * 复制文件
	 * @param files1 原文件路径
	 * @param FolderName 复制文件路径
	 * @throws IOException
	 */
	public static void copyFile(String files1,String  FolderName) throws IOException {
		File file=new File(files1);
		File file1=new File(FolderName+File.separator+file.getName());
		BufferedInputStream bi=new BufferedInputStream(new FileInputStream(file));
		BufferedOutputStream bo=new BufferedOutputStream(new FileOutputStream(file1));
		int i;
		byte[] by= new byte[20];
		while ((i=bi.read(by))!=-1) {
			bo.write(by, 0, i);
		}
		bo.close();
		bi.close();
	}

	/**
	 * 获取文件后缀
	 * @param fileName 文件名
	 * @return
	 */
	public static String getFileType(String fileName){
		String fileTyle=fileName.substring(fileName.lastIndexOf(".")+1,fileName.length());
		return fileTyle;
	}


    /**
     * 上传mxl文件
     * @param filepath 地址
     * @param filename 名字
     * @param xml  xml内容
     */
	public static void impxml(String filepath,String filename,String xml) {
		File targetFile = new File(filepath);
		if (!targetFile.exists()) {
			targetFile.mkdirs();
		}
		File writename = new File(filepath+filename);
		BufferedWriter out = null;
		try {
			writename.createNewFile(); // 创建新文件
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(writename),"UTF-8"));
			out.write(xml); // \r\n即为换行
			out.flush(); // 把缓存区内容压入文件
			out.close(); // 关闭输出
		} catch (IOException e) {
			System.out.println("文件输入错误");
			e.printStackTrace();
		}
	}

	/**
	 * 文件读取
	 * @return
	 */
	public static String impread(String pathname) {
		String code  = "";
		try {
			code = resolveCode(pathname);//获取txt文档编码
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		File filename = new File(pathname); //读取文件
		InputStreamReader reader = null;
		BufferedReader br = null;
		try {
			try {
				reader = new InputStreamReader(new FileInputStream(filename),code);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} //输入流
			br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			String line = "";
			StringBuilder xmldata = new StringBuilder();
			while ((line = br.readLine()) != null) {
				xmldata.append(line);
			}
			return xmldata.toString();
		} catch (IOException e) {
			System.out.println("文件读取错误");
		}
		return "";
	}

	/**
	 * 文件验证
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static String resolveCode(String path) throws Exception {
		InputStream inputStream = new FileInputStream(path);
		byte[] head = new byte[3];
		inputStream.read(head);
//		String code = "gb2312";  //或GBK
		String code = "UTF-8";
		if (head[0] == -1 && head[1] == -2 )
			code = "UTF-16";
		else if (head[0] == -2 && head[1] == -1 )
			code = "Unicode";
		else if(head[0]==-17 && head[1]==-69 && head[2] ==-65)
			code = "UTF-8";
		inputStream.close();
		return code;
	}

}
