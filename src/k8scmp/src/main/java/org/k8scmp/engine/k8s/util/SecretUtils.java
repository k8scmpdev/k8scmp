package org.k8scmp.engine.k8s.util;

import org.apache.shiro.codec.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.k8scmp.appmgmt.domain.ContainerDraft;
import org.k8scmp.common.GlobalConstant;
import org.k8scmp.globalmgmt.dao.GlobalBiz;
import org.k8scmp.globalmgmt.domain.RegisterInfo;
import org.k8scmp.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 */
@Component
public class SecretUtils {

    private static GlobalBiz globalBiz;

    @Autowired
    public void setGlobalBiz(GlobalBiz globalBiz) {
        SecretUtils.globalBiz = globalBiz;
    }

    public static boolean haveRegistry(
            List<ContainerDraft> containerDrafts) {
        if (containerDrafts == null) {
            return false;
        }
        RegisterInfo registry = globalBiz.getRegistry();

        if (registry == null ) {
            return false;
        }
        String registryUrl = registry.registryDomain();
        for (ContainerDraft containerDraft : containerDrafts) {
            if (!StringUtils.isBlank(containerDraft.getRegistry())
                    && containerDraft.getRegistry().contains(registryUrl)) {
                return true;
            }
        }
        return false;
    }

    public static String getImageSecretData() throws JSONException {
        JSONObject json = new JSONObject();
        JSONObject jsonAuths = new JSONObject();
        JSONObject jsonAuth = new JSONObject();
        RegisterInfo registry = globalBiz.getRegistry();
        if (registry == null) {
            return "";
        }
        String registryUrl = registry.registryDomain();
        jsonAuth.put("auth", GlobalConstant.REGISTRY_TOKEN);
        jsonAuth.put("email", GlobalConstant.REGISTRY_EMAIL);
        jsonAuths.put(registryUrl, jsonAuth);
        json.put("auths", jsonAuths);

        return Base64.encodeToString(json.toString().getBytes());
    }
}
