package com.acnt.test.ssl;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Auser on 2016/1/12.
 */
public class ReceiveImage2 {
    public static final String DOWNLOAD_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/downloads/";
    private ServerSocket ss;
    private Thread listenThread;
    //图片开始的标识

    private final String IMAGE_START = "image:";
    /**
     19      * 一条完整信息结束的标识
     20      */
    private final String MESSAGE_END = "over";
    /**
     23      * 文件名结束的表示
     24      */
    private final String FILE_NAME_END = "?";
    /**
     27      * 默认的编码，我的是UTF-8，大家可以更改成自己的编码
     28      */
    private final String DEFAULT_ENCODE = "UTF-8";
    /**
     31      * ISO编码
     32      */
    private final String ISO_ENCODE = "ISO-8859-1";
    public ReceiveImage2() throws Exception
    {
        ss = new ServerSocket(1314);

        listenThread = new Thread(new Runnable()
        {

            public void run()
            {
                listen();
            }
        });

        listenThread.start();
    }

    /**
     52      * 监听链接
     53      */
    private void listen()
    {
        while (!ss.isClosed())
        {
            try
            {
                final Socket s = ss.accept();
                new Thread(new Runnable()
                {

                    public void run()
                    {
                        read(s);
                    }
                }).start();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     78      * 读取信息
     socket
     客户端链接过来的socket
     82      */
    private void read(Socket socket)
    {
        try
        {
            InputStream is = socket.getInputStream();
            StringBuffer sb = new StringBuffer();
            int imageName = 0;
            while (!socket.isClosed())
            {
                int imageStart;
                while ((imageStart = sb.indexOf(IMAGE_START)) < 0)
                    readToBuffer(is, sb);

                System.out.println("开始读取第一部分文本信息");
                String text1 = sb.substring(0, imageStart);
                text1 = new String(text1.getBytes(ISO_ENCODE), DEFAULT_ENCODE);
                System.out.println("第一部分文本信息:" + text1);
                sb.delete(0, imageStart + IMAGE_START.length());

                System.out.println("开始读取文件名称");
                int file_name_end;
                while ((file_name_end = sb.indexOf(FILE_NAME_END)) < 0)
                    readToBuffer(is, sb);
                String file_name = new String(sb.substring(0, file_name_end).getBytes(ISO_ENCODE), DEFAULT_ENCODE);
                System.out.println("文件名称:" + file_name);
                sb.delete(0, file_name_end + FILE_NAME_END.length());

                System.out.println("开始读取文件长度");
                while (sb.length() < 8)
                    readToBuffer(is, sb);
                String imageLengthString = sb.substring(0, 8);
                byte[] imageLengthByteArray = imageLengthString.getBytes(ISO_ENCODE);
                long imageLength = bytesToLong(imageLengthByteArray);
                System.out.println("文件长度:" + imageLength);
                sb.delete(0, 8);

                System.out.println("开始读取文件");
                byte[] image = sb.toString().getBytes(ISO_ENCODE);
                FileOutputStream fos = new FileOutputStream(new File("DOWNLOAD_PATH " + imageName + file_name));
                //F:/接收文件
                if (imageLength > image.length)
                {
                    System.out.println("文件只有部分在数组中");
                    fos.write(image);
                    System.out.println("已经写了" + image.length + "还需要写" + (imageLength - image.length));
                    writeImage(is, fos, imageLength - image.length);
                    sb.delete(0, sb.length());
                }
                else
                {
                    System.out.println("文件已经在数组中");
                    fos.write(image, 0, (int) imageLength);
                    sb.delete(0, (int) imageLength);
                }
                fos.close();
                imageName++;
                System.out.println("文件已经保存");

                int end;
                while ((end = sb.indexOf(MESSAGE_END)) < 0)
                {
                    readToBuffer(is, sb);
                }
                String text2 = new String(sb.substring(0, end).getBytes(ISO_ENCODE), DEFAULT_ENCODE);
                System.out.println("第二部分文本信息:" + text2);
                sb.delete(0, end + MESSAGE_END.length());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {

            }
            System.out.println("线程结束");
        }

    }

    /**
     171      * 将输入流中的数据读取到stringbuffer中，一次最多读取1024个长度
     174      *            输入流
     176      *            图片文件输出流
     177      * @throws Exception
     178      */
    private void readToBuffer(InputStream is, StringBuffer sb) throws Exception
    {
        int readLength;
        byte[] b = new byte[1024];

        readLength = is.read(b);
        if (readLength == -1)
            throw new RuntimeException("读取到了-1，说明Socket已经关闭");
        String s = new String(b, 0, readLength, ISO_ENCODE);
        sb.append(s);
    }

    /**
     192      * 从输入流中读取图片信息到图片文件输出流中
     195      *            输入流
     197      *            图片文件输出流
     199      *            需要读取的数据长度
     201      */
    private void writeImage(InputStream is, FileOutputStream fos, long length) throws Exception
    {
        byte[] imageByte = new byte[1024];
        int oneTimeReadLength;

        for (long readLength = 0; readLength < length;)
        {
            if (readLength + imageByte.length <= length)
            {
                System.out.println("剩余的字节数大于1024，将尽可能多的读取内容");
                oneTimeReadLength = is.read(imageByte);
            }
            else
            {
                System.out.println("剩余的字节数小于1024，将只读取" + (length - readLength) + "字节");
                oneTimeReadLength = is.read(imageByte, 0, (int) (length - readLength));
            }

            if (oneTimeReadLength == -1)
                throw new RuntimeException("读取文件时，读取到了-1，说明Socket已经结束");
            System.out.println("实际读取长度" + oneTimeReadLength + "字节");

            readLength += oneTimeReadLength;

            fos.write(imageByte, 0, oneTimeReadLength);
            System.out.println("继续追加" + readLength + "字节长度");
        }
    }

    /**
     232      * 将byte数组转化为Long类型
     236      */
    public static long bytesToLong(byte[] array)
    {
        return ((((long) array[0] & 0xff) << 56) | (((long) array[1] & 0xff) << 48) | (((long) array[2] & 0xff) << 40)
                | (((long) array[3] & 0xff) << 32) | (((long) array[4] & 0xff) << 24)
                | (((long) array[5] & 0xff) << 16) | (((long) array[6] & 0xff) << 8) | (((long) array[7] & 0xff) << 0));
    }


}
