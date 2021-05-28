package com.cherry.consumer.service;

import com.cherry.api.UserApi;
import com.cherry.commons.tmplates.SmsTemplate;
import com.cherry.consumer.utils.MD5Util;
import com.cherry.consumer.utils.RandomUtil;
import com.cherry.consumer.utils.UuidUtils;
import com.cherry.domain.db.User;
import com.cherry.domain.pojo.VerifyCode;
import com.cherry.domain.vo.ErrorResult;
import com.cherry.domain.vo.UserVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {
    @Reference
    private UserApi userApi;

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    private String redisKey = "CODE_KEY_";

    //图片验证码生成
    public void verifyCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        VerifyCode vc = new VerifyCode();

        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(vc.getWidth(), vc.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics gd = buffImg.getGraphics();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 将图像填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, vc.getWidth(), vc.getHeight());

        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.BOLD, vc.getFontHeight());
        // 设置字体。
        gd.setFont(font);

        // 画边框。
        gd.setColor(Color.BLACK);
        gd.drawRect(0, 0, vc.getWidth() - 1, vc.getHeight() - 1);

        // 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
        gd.setColor(Color.BLACK);
        for (int i = 0; i < 20; i++) {
            int x = random.nextInt(vc.getWidth());
            int y = random.nextInt(vc.getHeight());
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            gd.drawLine(x, y, x + xl, y + yl);
        }
        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;
        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < vc.getCodeCount(); i++) {
            // 得到随机产生的验证码数字。
            String code = String.valueOf(vc.getCodeSequence()[random.nextInt(34)]);
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red, green, blue));
            gd.drawString(code, (i + 1) * vc.getXx(), vc.getCodeY());
            // 将产生的四个随机数组合在一起。
            randomCode.append(code);
        }
        // 将四位数字的验证码保存到Session中。
        HttpSession session = req.getSession();
        System.out.println(randomCode);
        session.setAttribute("code", randomCode.toString());
        // 禁止图像缓存。
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);
        resp.setContentType("image/jpeg");
        // 将图像输出到Servlet输出流中。
        ServletOutputStream sos = resp.getOutputStream();
        ImageIO.write(buffImg, "jpeg", sos);
        sos.close();
    }

    //发送短信验证码
    public ResponseEntity sendCode(String mobile) {
        //生成6位手机验证码
        String sixBitRandom = RandomUtil.getSixBitRandom();
        //存入redis手机验证码,并设置5分钟失效时间
        redisTemplate.opsForValue().set(redisKey+mobile,sixBitRandom,5,TimeUnit.MINUTES);
        System.out.println(sixBitRandom);
        return ResponseEntity.ok().body("验证码发送成功");
    }

    //保存用户
    public ResponseEntity saveUser(Map map) throws Exception {
        UserVo userVo = getObject(map, UserVo.class);
        User userDb = new User();

        String phone = userVo.getMobile();
        String code = userVo.getCode();
        String username = userVo.getUsername();

        //用户名+手机非空判断
        if (phone == null || username == null){
            return ResponseEntity.status(500).body(ErrorResult.phoneError());
        }
        String value = (String) redisTemplate.opsForValue().get(redisKey + phone);
        //验证码非空判断
        if (value == null || !value.equals(code)){
            return ResponseEntity.status(500).body(ErrorResult.codeRrror());
        }
        //判断系统内是否有重复的用户名或者手机号
        int isUserNull = userApi.queryUserIsNull(username);
        if (isUserNull >= 1){
            return ResponseEntity.status(500).body("你输入的用户已经存在");
        }
        int isPhoneNull = userApi.queryPhoneIsNull(phone);
        if (isPhoneNull >= 1){
            return ResponseEntity.status(500).body("你输入的手机号已经存在");
        }

        //删除Redis中的code
        redisTemplate.delete(redisKey+phone);
        userDb.setId(UuidUtils.getId());
        userDb.setUsername(username);
        userDb.setMobile(phone);
        //生成加密MD5
        String encryptedPwd = MD5Util.getEncryptedPwd(userVo.getPassword());
        userDb.setPassword(encryptedPwd);
        userDb.setCreated(new Date());
        userApi.saveUser(userDb);
        return ResponseEntity.ok().body(ErrorResult.success());
    }

    //用户登录
    public ResponseEntity login(Map map) throws Exception {
        UserVo userVo = getObject(map,UserVo.class);
        String username = userVo.getUsername();
        String password = userVo.getPassword();

        String queryPassword = userApi.queryUser(username);

        if (!password.equals(queryPassword)){
            System.out.println("验证失败");
            return null;
        }
        System.out.println("验证成功");
        return ResponseEntity.ok("验证成功");
    }
    //将集合转化为对象的具体实现
    private static <T>T getObject(Map<String,Object> map, Class<T> c) throws Exception {
        T t = c.newInstance();//创建一个一个类型为T对象t
        //1.拆开map
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {//获取集合里面的元素
            String key = entry.getKey();//得到key的值（类T的的成员属性）
            //2.将map中的值存入T这个类的对象属性中
            Field f = c.getDeclaredField(key);//获取类的所有字段
            f.setAccessible(true);//简单的理解：设置访问权限
            f.set(t,entry.getValue());//给T对象赋值
        }
        return t;
    }
}
