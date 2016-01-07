package com.acnt.test.ssl;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyStore;
import java.util.UUID;

import javax.net.SocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
	Activity act = this;

	private String TAG = this.getClass().getSimpleName();
	private EditText edit;//
	private String content="";//内容
	private String text = "";//显示文本
	private TextView textView;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		textView=(TextView)findViewById(R.id.textview);
		edit=(EditText)findViewById(R.id.edit);
		edit.addTextChangedListener(new TextWatcher() {
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				content=edit.getText().toString();//获取输入框内容
			}
			public void afterTextChanged(Editable s) {

			}
		});
	}

	public void testSSL(View view) {
		new MyNetWorkTask().execute(new String[] {content});
	}

	class MyNetWorkTask extends AsyncTask<String, Void, String> {

		ProgressDialog pd;
		DataInputStream reader;
		DataOutputStream writer;
		protected void onPreExecute() {
			pd = DialogUtil.showProgress(act);
			super.onPreExecute();
			Log.i(TAG, "onPreExecute");
		}


		protected String doInBackground(String... params) {
			String text=null;
			Log.i(TAG, "doInBackground");

			try {
				SSLContext context;
				KeyStore ts = KeyStore.getInstance("BKS");
				ts.load(getResources().openRawResource(R.raw.test), "1qaz2wsx".toCharArray());
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("X509");
				tmf.init(ts);
				TrustManager[] tm = tmf.getTrustManagers();
				context = SSLContext.getInstance("SSL");
				context.init(null, tm, null);
				SocketFactory factory = context.getSocketFactory();
				SSLSocket socket = (SSLSocket) factory.createSocket("192.168.254.179", 1314);

				DataOutputStream out = new DataOutputStream(socket.getOutputStream());
//				out.writeUTF(new String("来自客户端的发送测试".getBytes(),"UTF-8"));
//				out.flush();
//"来自客户端的发送测试".getBytes()

				String test = "内容长度:"+params[0].length()+"\n"+"传输方式:post\n"
						+"content="+params[0]+" ";
				out.writeUTF(new String(test.getBytes(), "UTF-8"));
				out.flush();
//				// 7：获取输出流
				reader=new DataInputStream(new BufferedInputStream(socket.getInputStream()));
				text= reader.readUTF();

//                DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()) );
//                String obj = in.readUTF();
//                byte[] b = obj.getBytes();
//                for (int i = 0; i < b.length; i++) {
//					Log.i(TAG, "doInBackground: obj = " + b[i]);
//                }
                socket.close();
				writer.close();
				reader.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			return text;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			pd.dismiss();
			Log.i(TAG, "onPostExecute");
			edit.setText("");
			text= text +'\n'+ result;
			Log.i(TAG, "onPostExecute: result=="+text);
			textView.setText(result+'\n'+ textView.getText());


		}
	}

}
