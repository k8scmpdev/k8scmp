package org.k8scmp.appmgmt.domain;

import org.k8scmp.util.StringUtils;

/**
 */
public class Env {
    private String key;
    private String value;
    private String description;

    public Env() {
    }

    public Env(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String checkLegality() {
        String error = null;
        if (!StringUtils.checkEnvNamePattern(key)) {
            error = "environment key must be a C identifier (matching regex [A-Za-z_][A-Za-z0-9_]*): e.g. \"my_name\" or \"MyName\"";
        }
        return error;
    }
}
