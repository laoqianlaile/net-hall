<%@ page contentType="image/jpeg" import="java.util.*,java.awt.*,java.io.*,java.awt.image.*,javax.imageio.* " %><%@ page import="com.yd.svrplatform.spring.ApplicationContextHelper" %>
<%@ page import="com.yd.basic.util.YDBASE64Encoder" %>
<%@ page import="com.yd.svrplatform.comm_mdl.param_config.ParamConfigImp"%>
<%@ page import="com.yd.ish.common.service.mybatis.ComVCodeService" %>

<%!
    Color getRandColor(int fc,int bc){//给定范围获得随机颜色
        Random random = new Random();
        if(fc>255) fc=255;
        if(bc>255) bc=255;
        int r=fc+random.nextInt(bc-fc);
        int g=fc+random.nextInt(bc-fc);
        int b=fc+random.nextInt(bc-fc);
        return new Color(r,g,b);
    }
%>
<%!
    /**
     * @Descriptionmap 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
     * @author liym
     * @Date 2016-04-17
     * @param file 图片文件
     * @return
     */
    public static String imageToBase64(File file) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;
        // 读取图片字节数组
        InputStream in = null;
        try {
            in =  new FileInputStream(file);
            data = new byte[in.available()];
            in.read(data);
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                if (in != null) in.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        // 对字节数组Base64编码
        YDBASE64Encoder encoder = new YDBASE64Encoder();

        String strBase64 = encoder.encode(data);
        return strBase64;// 返回Base64编码过的字节数组字符串
    }
%>
<%
    //设置页面不缓存
    response.setHeader("Pragma","No-cache");
    response.setHeader("Cache-Control","no-cache");
    response.setDateHeader("Expires", 0);
    response.setContentType("text/plain");
// 在内存中创建图象    
    int width=60, height=20;
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

// 获取图形上下文    
    Graphics g = image.getGraphics();
//生成随机类    
    Random random = new Random();

// 设定背景色    
    g.setColor(getRandColor(200,250));
    g.fillRect(0, 0, width, height);

//设定字体    
    g.setFont(new Font("Times New Roman",Font.PLAIN,18));

//画边框    
//g.setColor(new Color());    
//g.drawRect(0,0,width-1,height-1);    

// 随机产生155条干扰线，使图象中的认证码不易被其它程序探测到    
    g.setColor(getRandColor(160,200));
    for (int i=0;i<155;i++)
    {
        int x = random.nextInt(width);
        int y = random.nextInt(height);
        int xl = random.nextInt(12);
        int yl = random.nextInt(12);
        g.drawLine(x,y,x+xl,y+yl);
    }

// 取随机产生的认证码(4位数字)    
    String sRand="";
    for (int i=0;i<4;i++){
        String rand=String.valueOf(random.nextInt(10));
//rand="0";//2011 8 20 hzy 为了配合贾凤的压力测试验证码改成了固定的
        sRand+=rand;
// 将认证码显示到图象中

        g.setColor(new Color(20+random.nextInt(110),20+random.nextInt(110),20+random.nextInt(110)));// 调用函数出来的颜色相同，可能是因为种子太接近，所以只能直接生成
        g.drawString(rand,13*i+6,16);
    }

// 2016 04 12 liym 将验证码的数据存放到Memcached 中。
    ParamConfigImp paramConfigImp=(ParamConfigImp)ApplicationContextHelper.getBean("paramConfigImp");
//	if(paramConfigImp.getVal("ish.gg.other.yzm")==null)
//	paramConfigImp.load("ish.gg.other.yzm")；
    int sjyzm_yxsc=10;
    String key = ComVCodeService.sendYzm(sRand,sjyzm_yxsc);
//YDMemcachedManager sm=YDMemcachedManager.newInstance();
//SimpleKeyGenerator simpleKeyGenerator = (SimpleKeyGenerator) ApplicationContextHelper.getBean("simpleKeyGenerator");
    //String key = "COM_YZM_"+"sjyzm"+simpleKeyGenerator.generator();
//sm.add(key, sRand, 1*60);
// 图象生效    
    g.dispose();
    String _contexPath = request.getContextPath().equals("/") ? "" : request.getContextPath();
    String filename="ish/sjyzm/temp"+Thread.currentThread().getId();
    System.out.println("fileName: "+filename);
    File imageFile = new File(filename);
    imageFile.mkdirs();
//将图片输出到临时文件中
    ImageIO.write(image, "JPEG", imageFile);
    String imageBase64 = imageToBase64(imageFile);
    imageBase64 = "data:image/jpeg;base64,"+imageBase64;
    String result = "{\"returnCode\": 0,\"image\":\""+imageBase64+"\",\"key\":\""+key+"\"}";
    System.out.println("验证码生成：" + result);
    out.print(result);
%> 