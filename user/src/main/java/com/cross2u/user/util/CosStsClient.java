package com.cross2u.user.util;

import com.qcloud.Module.Sts;
import com.qcloud.QcloudApiModuleCenter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.TreeMap;

/**
 * 存储图片到云上
 */
    public class CosStsClient {
        private static final String POLICY = "{\"statement\": [{\"action\": [\"name/cos:*\"],\"effect\": \"allow\",\"resource\":\"*\"}],\"version\": \"2.0\"}";
        private static final int DEFAULT_DURATION_SECONDS = 1800;

        public static JSONObject getCredential(TreeMap<String, Object> config) throws IOException {
            config.put("RequestMethod", "GET");

            QcloudApiModuleCenter module = new QcloudApiModuleCenter(new Sts(), config);

            TreeMap<String, Object> params = new TreeMap<String, Object>();

            params.put("name", "tac-storage-sts-java");
            String policy = config.get("policy") == null ? POLICY : (String) config.get("policy");
            params.put("policy", policy);
            int durationInSeconds = config.get("durationInSeconds") == null ? DEFAULT_DURATION_SECONDS :
                    (Integer) config.get("durationInSeconds");
            params.put("durationSeconds", durationInSeconds);

            try {
                /* call 方法正式向指定的接口名发送请求，并把请求参数 params 传入，返回即是接口的请求结果。 */
                String result = module.call("GetFederationToken", params);
                return new JSONObject(result);
            } catch (Exception e) {
                System.out.println("error..." + e.getMessage());
                e.printStackTrace();
            }

            return null;
        }

        public static String getPolicy(List<Scope> scopes) {
            if(scopes == null || scopes.size() == 0)return null;
            STSPolicy stsPolicy = new STSPolicy();
            stsPolicy.addScope(scopes);
            return stsPolicy.toString();
        }

        // v2接口的key首字母小写，v3改成大写，此处做了向下兼容
        private static JSONObject downCompat(JSONObject resultJson) {
            JSONObject dcJson = new JSONObject();

            for (String key : resultJson.keySet()) {
                Object value = resultJson.get(key);
                if (value instanceof JSONObject) {
                    dcJson.put(headerToLowerCase(key), downCompat((JSONObject) value));
                } else {
                    String newKey = "Token".equals(key) ? "sessionToken" : headerToLowerCase(key);
                    dcJson.put(newKey, resultJson.get(key));
                }
            }

            return dcJson;
        }

        private static String headerToLowerCase(String source) {
            return Character.toLowerCase(source.charAt(0)) + source.substring(1);
        }

        private static JSONObject getPolicy(TreeMap<String, Object> config) {
            String bucket = (String) config.get("bucket");
            String region = (String) config.get("region");
            String allowPrefix = (String) config.get("allowPrefix");
            String[] allowActions = (String[]) config.get("allowActions");

            JSONObject policy = new JSONObject();
            policy.put("version", "2.0");

            JSONObject statement = new JSONObject();
            policy.put("statement", statement);

            statement.put("effect", "allow");
            JSONObject principal = new JSONObject();
            principal.put("qcs", "*");
            statement.put("principal", principal);

            JSONArray actions = new JSONArray();
            for (String action : allowActions) {
                actions.put(action);
            }
            statement.put("action", actions);

            int lastSplit = bucket.lastIndexOf("-");
            String shortBucketName = bucket.substring(0, lastSplit);
            String appId = bucket.substring(lastSplit + 1);

            String resource = String.format("qcs::cos:%s:uid/%s:prefix//%s/%s/%s",
                    region, appId, appId, shortBucketName, allowPrefix);
            statement.put("resource", resource);

            return policy;

        }
    }

