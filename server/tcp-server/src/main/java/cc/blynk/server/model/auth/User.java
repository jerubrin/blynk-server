package cc.blynk.server.model.auth;

import cc.blynk.common.stats.GlobalStats;
import cc.blynk.server.model.UserProfile;
import cc.blynk.server.utils.JsonParser;
import com.codahale.metrics.Meter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * User: ddumanskiy
 * Date: 8/11/13
 * Time: 4:03 PM
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String pass;

    private String id;

    //used mostly to understand if user profile was changed, all other fields update ignored as it is not so important
    private long lastModifiedTs;

    private UserProfile userProfile;

    private Map<Integer, String> dashTokens = new HashMap<>();

    //maybe it is bette rto make it transient
    private Stats stats;

    private transient Meter quotaMeter;

    public User() {
        this.lastModifiedTs = System.currentTimeMillis();
        this.userProfile = new UserProfile();
    }

    public User(String name, String pass) {
        this();
        this.name = name;
        this.pass = pass;
        this.stats = new Stats();
        initQuota();
    }

    public void initQuota() {
        this.quotaMeter = GlobalStats.metricRegistry.meter(this.name);
    }

    public void incrStat(short cmd) {
        stats.incr(cmd);
        quotaMeter.mark(1);
    }

    public void incrException(int exceptionCode) {
        stats.incrException(exceptionCode);
        quotaMeter.mark(1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public void putToken(Integer dashId, String token) {
        this.dashTokens.put(dashId, token);
        this.lastModifiedTs = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<Integer, String> getDashTokens() {
        return dashTokens;
    }

    public void setDashTokens(Map<Integer, String> dashTokens) {
        this.dashTokens = dashTokens;
    }

    public Stats getStats() {
        return stats;
    }

    public Meter getQuotaMeter() {
        return quotaMeter;
    }

    public long getLastModifiedTs() {
        return lastModifiedTs;
    }

    public void setLastModifiedTs(long lastModifiedTs) {
        this.lastModifiedTs = lastModifiedTs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (name != null ? !name.equals(user.name) : user.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return JsonParser.toJson(this);
    }
}
