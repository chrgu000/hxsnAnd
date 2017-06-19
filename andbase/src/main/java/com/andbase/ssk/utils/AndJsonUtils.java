package com.andbase.ssk.utils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.andbase.ssk.entity.AnswerInfo;
import com.andbase.ssk.entity.AppVersion;
import com.andbase.ssk.entity.Nongsh;
import com.andbase.ssk.entity.NotifyInfo;
import com.andbase.ssk.entity.QuestionInfo;
import com.andbase.ssk.entity.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by jiely on 2017/1/18.
 */

public class AndJsonUtils {

    private static String[] params;


    public static String getResult(String jsonString){
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String result = jsonObject.optString("result");
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getDescription(String jsonString){
        String desc;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            desc = jsonObject.optString("description");
            return desc;
        } catch (JSONException e) {
            String msg = jsonObject.optString("msg");
            e.printStackTrace();
            return msg;
        }

    }

    public static String getCode(String jsonString){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            return jsonObject.getString("code");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getStatus(String jsonString){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            return jsonObject.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getCreateTime(String jsonString){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            return jsonObject.getLong("createTime");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static boolean isSuccess(String jsonString){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            String code = jsonObject.getString("code");
            if(code.equals("200")){
                return true;
            }else {
                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 给user加上cms信息
     * @param jsonString 后台接收到的json
     * @param user       要添加信息的user
     * @return user 设置完成后的user
     */
    public static User setCmsUser(String jsonString,User user){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.optJSONObject("user");
            if(user != null){
                String userWentype = jsonObject.optString("userwentype");
                String userRole = jsonObject.optString("userrole");
                user.setUserExpertId(userWentype);
                user.setUserRole(userRole);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * desc:user解析
     * auther:jiely
     * create at 2015/10/10 19:52
     */
    public static User getUser(String jsonString) {
        params = new String[]{"userid", "username", "realname", "nickname","email", "phone", "address"};
        User user = null;
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.optJSONObject("result");
            String userId = jsonObject.optString(params[0]);
            user = new User();
            user.setUserId(userId);
            String userName = jsonObject.optString(params[1]);
            user.setUserName(userName);
            String realName = jsonObject.optString(params[2]);
            user.setRealName(realName);
            String nickName = jsonObject.optString(params[3]);
            user.setNickName(nickName);
            String email = jsonObject.optString(params[4]);
            user.setEmail(email);
            String phone = jsonObject.optString(params[5]);
            user.setPhone(phone);
            String address = jsonObject.optString(params[6]);
            user.setAddress(address);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }




    /**
     * desc:通知消息解析
     * auther:jiely
     * create at 2016/5/6 10:52
     */
    public static NotifyInfo getNotifyInfo(String jsonString) {
        params = new String[]{"id","type","open_type","description","title","notification_builder_id","notification_basic_style"};
        NotifyInfo notifyInfo = new NotifyInfo();
        if(TextUtils.isEmpty(jsonString)){
            return null;
        }
        try {
            JSONObject jsonObject = new JSONObject(jsonString);

            String id = jsonObject.optString(params[0]);
            notifyInfo.setId(id);
            int type = jsonObject.optInt(params[1]);
            notifyInfo.setType(type);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return notifyInfo;
    }



    /**
     * json解析 获取问题列表
     * @param  jsonString json串
     * @return 气象站列表
     */
    @NonNull
    public static List<QuestionInfo> getQuestionList(String urlHead,String jsonString){
        List<QuestionInfo> questionList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.optJSONArray("questionList");

            if(jsonArray == null){
                return questionList;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                QuestionInfo info = new QuestionInfo();
                info.setId(jsonObject.optString("wzjid"));
                info.setUsername(jsonObject.optString("wzjname"));
                info.setTime(jsonObject.optString("time"));
                info.setTitle(jsonObject.optString("title"));
                String url = jsonObject.optString("imgUrl");
                if(url.length()>0){
                    info.setUrl(urlHead+url);
                }else {
                    info.setUrl("");
                }

                LogUtil.i("JsonUtils","url="+url);
                int isReplied = jsonObject.optInt("isReplied");
                if(isReplied == 0){
                    info.setIsResponse(jsonObject.optInt("isReplied"));
                }else {
                    JSONArray jsonArrayTemp = jsonObject.optJSONArray("answerList");
                    if(jsonArrayTemp == null){
                        break;
                    }
                    List<AnswerInfo> answerInfoList = new ArrayList<>();
                    for (int j = 0; j < jsonArrayTemp.length(); j++) {
                        AnswerInfo answerInfo = new AnswerInfo();
                        JSONObject jsonObjectTemp = jsonArrayTemp.getJSONObject(j);
                        answerInfo.setId(jsonObjectTemp.optString("hfid"));
                        answerInfo.setTime(jsonObjectTemp.optString("hftime"));
                        answerInfo.setContent(jsonObjectTemp.optString("hfcontent"));
                        answerInfo.setName(jsonObjectTemp.optString("hfusername"));
                        answerInfoList.add(answerInfo);
                    }
                    info.setAnswerList(answerInfoList);
                }

                questionList.add(info);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return questionList;
    }

    /**
     * json解析 获取问题回复列表
     * @param  jsonString json串
     * @return 气象站列表
     */
    public static List<AnswerInfo> getAnswerList(String jsonString){
        List<AnswerInfo> answerInfoList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.optJSONObject("question");
            JSONArray jsonArray = jsonObject.optJSONArray("answerList");
            if(jsonArray == null){
                return answerInfoList;
            }
            for (int i = 0; i < jsonArray.length(); i++) {
                AnswerInfo info = new AnswerInfo();
                jsonObject = jsonArray.getJSONObject(i);
                info.setId(jsonObject.optString("hfid"));
                info.setTime(jsonObject.optString("hftime"));
                info.setContent(jsonObject.optString("hfcontent"));
                info.setName(jsonObject.optString("hfusername"));
                answerInfoList.add(info);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return answerInfoList;
    }


    /**
     * desc:从后台获取APP版本信息
     * auther:jiely
     * create at 2015/10/10 19:52
     */
    public static AppVersion getAppVersion(String jsonString) {
        LogUtil.i("JsonUtils","jsonString="+jsonString);
        params = new String[]{"createDate", "description", "version", "url","nshversion"};
        AppVersion appVersion = new AppVersion();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONObject jsonVersion = jsonObject.optJSONObject("result");
            if(jsonVersion == null){
                return appVersion;
            }
            String createDate = jsonVersion.optString(params[0]);
            appVersion.setCreateDateString(createDate);
            String description = jsonVersion.optString(params[1]);
            appVersion.setDesc(description);
            String strVersion = jsonVersion.optString(params[2]);
            appVersion.setVersion(strVersion);
            String url = jsonVersion.optString(params[3]);
            appVersion.setUrl(url);
            int nshVersion = jsonVersion.optInt(params[4]);
            appVersion.setNshversion(nshVersion);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return appVersion;
    }

    private static final String[] nongshes = {"id", "name", "image"};

    public static List<Nongsh> getNongshList(String urlHead,String jsonString){
        List<Nongsh> nongshList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            jsonObject = jsonObject.optJSONObject("result");
            JSONArray jsonArray = jsonObject.optJSONArray("channellist");
            if(jsonArray == null){
                return nongshList;
            }
            for(int i=0; i<jsonArray.length();i++){
                jsonObject = jsonArray.optJSONObject(i);
                String id = jsonObject.optString(nongshes[0]);
                String name = jsonObject.optString(nongshes[1]);
                String image = jsonObject.optString(nongshes[2]);

                if(image.length()>0){
                    image = urlHead+image;
                }

                Nongsh nongsh = new Nongsh(id,name,image);
                nongshList.add(nongsh);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return nongshList;
    }




}
