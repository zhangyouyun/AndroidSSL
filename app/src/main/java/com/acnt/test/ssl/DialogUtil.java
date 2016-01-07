package com.acnt.test.ssl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtil {

	public static void showDialog(Context context, String title,
			String message, DialogInterface.OnClickListener yes,
			DialogInterface.OnClickListener no) {

		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
				.setPositiveButton("确定", yes).setNegativeButton("取消", no)
				.create().show();
	}
	
	public static void showDialog(Context context, String title,
			int message, DialogInterface.OnClickListener yes,
			DialogInterface.OnClickListener no) {
		
		new AlertDialog.Builder(context).setTitle(title).setMessage(message)
		.setPositiveButton("确定", yes).setNegativeButton("取消", no)
		.create().show();
	}
	
	
	public static ProgressDialog showProgress(Context context){
		return showProgress(context, "加载数据..");
	}
	
	public static ProgressDialog showProgress(Context context,String message){
		return ProgressDialog.show(context, null, message);
	}
	
	
}
