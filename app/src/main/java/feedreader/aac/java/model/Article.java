package feedreader.aac.java.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.Root;

import java.util.Date;

import feedreader.aac.java.App;

@Entity
@Root(name = "item", strict = false)
@SuppressWarnings("unused")
public class Article {

    @PrimaryKey
    @Element(name = "guid")
    private String guid;

    @Element(name = "title")
    private String title;

    @Element(name = "link")
    private String link;

    @Element(name = "pubDate")
    private Date published;

    @Element(name = "encoded", data = true)
    @Namespace(prefix = "content", reference = "http://purl.org/rss/1.0/modules/content/")
    private String html;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPublished() {
        return published;
    }

    public void setPublished(Date published) {
        this.published = published;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    // The guid is in the form of "61046 at http://www.androidcentral.com" -- the number at the beginning is
    // unique and the rest is the same for every article. So, we split the string on the first space and try
    // to parse it as a Long. If that fails for any reason, we use a hashcode of the guid. If guid is null,
    // we return NO_ID. We use this id in the RecyclerView for the itemId.
    public long id() {
        String[] list = TextUtils.isEmpty(guid) ? null :guid.split(" ", 2);
        if (list != null && list.length > 0) {
            return TextUtils.isDigitsOnly(list[0]) ? Long.valueOf(list[0]) : guid.hashCode();
        } else {
            return RecyclerView.NO_ID;
        }
    }

    public String relativeDate() {
        final Date pub = published;
        return pub == null ? "" : DateUtils.getRelativeDateTimeString(App.current, pub.getTime(),
                DateUtils.MINUTE_IN_MILLIS, DateUtils.DAY_IN_MILLIS, 0).toString();
    }
}
