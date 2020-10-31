package com.web.mundo.vo;

import lombok.Data;

import java.util.Objects;

/**
 * @author chenhao
 * @title: ProxyVO
 * @projectName mundo
 * @description: 代理对象
 * @date 2019/12/22  16:58
 */
public class ProxyVO {

    private String ip;
    private int port;
    private Long validDate;
    private Long lockDate;

    public Long getValidDate() {
        return validDate;
    }

    public void setValidDate(Long validDate) {
        this.validDate = validDate;
    }

    public Long getLockDate() {
        return lockDate;
    }

    public void setLockDate(Long lockDate) {
        this.lockDate = lockDate;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProxyVO proxyVO = (ProxyVO) o;
        return port == proxyVO.port &&
                Objects.equals(ip, proxyVO.ip);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ip, port);
    }

    @Override
    public String toString() {
        return "ProxyVO{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
