package com.acnt.test.ssl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.security.KeyStore;
import java.util.UUID;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {
	Activity act = this;
	private String path;
	private String host;
	private String TAG = this.getClass().getSimpleName();
	private EditText edit;//
	private String content="";//内容
	private String text ="";
	private TextView textView;//显示文本
	private String http=""; //存放http协议
    public static final String DOWNLOAD_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/downloads/";
    private ImageView imageview;
    byte[] inputByte = null;
    int length = 0;
    Bitmap bitmap;
    private ImageView image;//图片
    //图片地址
    private String imageUrl = "DOWNLOAD_PATH";
	private Button getmessage;
	private Button getimage;
	private Button getother;
	private static final int message=1;
	private static final int mImage=2;
	private static final int other=3;
	private String choice;
	String line = null;
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView=(TextView)findViewById(R.id.textview);
//		edit=(EditText)findViewById(R.id.edit);
        imageview=(ImageView)findViewById(R.id.show_image);
		getmessage=(Button)findViewById(R.id.getmessage);
		getimage=(Button)findViewById(R.id.getimage);
		getother=(Button)findViewById(R.id.getother);
		getmessage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				choice="message";
				MyNetWorkTask myNetWorkTask=new MyNetWorkTask();
				myNetWorkTask.execute();
			}
		});
		getimage.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				choice="image";
				MyNetWorkTask myNetWorkTask=new MyNetWorkTask();
				myNetWorkTask.execute();
			}
		});
		getother.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				choice = "other";
				MyNetWorkTask myNetWorkTask = new MyNetWorkTask();
				myNetWorkTask.execute();
			}
		});

    }

	public void start(View view){
		Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivity(intent);

	}
	public void testSSL(View view) {
		new MyNetWorkTask().execute(new String[]{content});
	}

	class MyNetWorkTask extends AsyncTask<String, Void, String> {

		ProgressDialog pd;
		DataInputStream reader;
        DataOutputStream writer;
        FileOutputStream fileOutputStream;
		protected void onPreExecute() {
			pd = DialogUtil.showProgress(act);
			super.onPreExecute();
			Log.i(TAG, "onPreExecute");
		}


		protected String doInBackground(String... params) {
			String result=null;
			Log.i(TAG, "doInBackground");

			try {
				//生成密钥
				SSLContext context;
				KeyStore ts = KeyStore.getInstance("BKS");

				ts.load(getResources().openRawResource(R.raw.sslkey), "123456".toCharArray());
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
				tmf.init(ts);
				TrustManager[] tm = tmf.getTrustManagers();
				context = SSLContext.getInstance("SSL");
				context.init(null, tm, null);
				SocketFactory factory = context.getSocketFactory();
				SSLSocket socket = (SSLSocket) factory.createSocket("192.168.254.92",8889 );

					if(choice.equals("message")){
						//

						writer = new DataOutputStream(socket.getOutputStream());
						http = "GET text HTTP/1.1\r\n";
//						http+= params[0];
						writer.writeUTF(http);
//						writer.writeBytes("GET text HTTP/1.1\r\n");
						Log.i("GET text HTTP/1.1\r\n","klll");
						reader = new DataInputStream(socket.getInputStream());
						result=reader.readUTF();
//						String test = "内容长度:"+params[0].length()+"\n"+"传输方式:post\n"
//						+"www.baidu.com"+params[0]+" ";
//						out.writeUTF(new String(test.getBytes(), "UTF-8"));
						writer.flush();
					}else if(choice.equals("image")){


//						File file=new File(DOWNLOAD_PATH+"psb.jpg");
//						DataInputStream inputStream=new DataInputStream(new FileInputStream(DOWNLOAD_PATH+"psb.jpg"));
//						FileOutputStream fileOnputStream = new FileOutputStream(file);
//
//						inputByte = new byte[1024];
//						while ((length = reader.read(inputByte, 0, inputByte.length)) > 0) {
//							writer.write(inputByte, 0, length);
//
//						}
//
//						Log.i("aaaaaa", "完成接收");
						writer = new DataOutputStream(socket.getOutputStream());
						http = "GET image HTTP/1.1\r\n";
//						http+= params[0];
						writer.writeUTF(http);
						File file=new File(DOWNLOAD_PATH+"psb.jpg");
						writer=new DataOutputStream(new FileOutputStream(DOWNLOAD_PATH+"psb.jpg"));
						reader=new DataInputStream(socket.getInputStream());
//						result= reader.readUTF();
						Log.i("aaaaaa", "\n开始接收数据...");
						inputByte = new byte[1024];
						while ((length = reader.read(inputByte)) > 0) {
							Log.i("aaaaaa",""+length);
							writer.write(inputByte, 0, length);
						}


					}else if(choice.equals("other")){
						writer = new DataOutputStream(socket.getOutputStream());
						http = "GET error HTTP/1.1\r\n";
						writer.writeUTF(http);
						reader = new DataInputStream(socket.getInputStream());
						result=reader.readUTF();
						writer.flush();

					}
				http="POST GetImage HTTP/1.1\n";
//				// 7：获取输出流

				socket.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}finally {
				try{
					if (writer != null)
						writer.close();
				}catch (Exception e){
					e.printStackTrace();
				}
				try{
					if (reader != null)
						reader.close();
				}catch (Exception e){
					e.printStackTrace();
				}

			}
			return result;
		}
        private void showImage(){
            FileInputStream fileOutputStream= null;
            try {
                fileOutputStream = new FileInputStream(DOWNLOAD_PATH+"psb.jpg");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
             bitmap = BitmapFactory.decodeStream(fileOutputStream);

			//
		}
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pd.dismiss();
//			edit.setText("");
			text= text +'\n'+ result;
			Log.i(TAG, "onPostExecute: result==" + text);
            textView.setText(result + '\n' + textView.getText());//显示发送的内容
            showImage();
             imageview.setImageBitmap(bitmap);



		}
	}

}
