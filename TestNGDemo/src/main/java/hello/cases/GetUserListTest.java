package hello.cases;

import com.alibaba.fastjson.JSONArray;
import hello.config.TestConfig;
import hello.dao.TestCaseDao;
import hello.model.GetUserInfoCase;
import hello.model.GetUserListCase;
import hello.model.User;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.Assert;

import java.io.IOException;
import java.util.*;

@SpringBootTest
public class GetUserListTest {
    @Autowired
    private TestCaseDao testCaseDao;

    public void testGetUserListTest() throws InterruptedException, IOException {
        //这里该怎么解决，不用手动传入参数
        Map<String, Object> param = new HashMap<>();
        param.put("id",1);
        GetUserListCase getUserListCase = testCaseDao.getUserList(param);


        JSONArray result = getResult(getUserListCase);

        //为什么要加这一条，有可能数据还没有
        Thread.sleep(2000);
        //验证数据的正确性，这是自己从数据库中查出来的User
        Map<String,Object> param1 = new HashMap<>();
        param.put("id",getUserListCase.getUserName());
        User userInfo = testCaseDao.queryUser(param);
        //为什么一定要放入List中？？？
        List userList = new ArrayList();
        userList.add(userInfo);
        JSONArray jsonArray = new JSONArray(userList);
        //需要转换成String
        Assert.assertEquals(jsonArray.toString(),result.toString());
    }

    /**
     * 获取结果,把结果转化成JSONObject
     * @return
     */
    private JSONArray getResult(GetUserListCase getUserListCase) throws IOException {
        HttpPost post = new HttpPost(TestConfig.getUserInfoUrl);
        org.json.JSONObject param = new org.json.JSONObject();
        param.put("id",getUserListCase.getUserName());
        //设置请求头信息 设置header
        post.setHeader("content-type","application/json");
        //将参数信息添加到方法中
        StringEntity entity = new StringEntity(param.toString(),"utf-8");
        post.setEntity(entity);
        //设置cookies
        TestConfig.defaultHttpClient.setCookieStore(TestConfig.cookieStore);
        //声明一个对象来进行响应结果的存储
        String result;
        //执行post方法
        HttpResponse response = TestConfig.defaultHttpClient.execute(post);
        //获取响应结果
        result = EntityUtils.toString(response.getEntity(),"utf-8");
        System.out.println("调用接口result:"+result);
        List resultList = Arrays.asList(result);
        JSONArray array = new JSONArray(resultList);
        System.out.println(array.toString());
        return array;
    }
}
