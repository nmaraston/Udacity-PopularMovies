package com.iopho.android.dataAccess.tmdb.model;

import android.support.annotation.StringDef;

import com.google.common.base.Preconditions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 */
public class VideoLink {

    public static class Type {
        public static final String TRAILER    = "Trailer";
        public static final String TEASER     = "Teaser";
        public static final String CLIP       = "Clip";
        public static final String FEATURETTE = "Featurette";

        @Retention(RetentionPolicy.SOURCE)
        @StringDef({
                TRAILER,
                TEASER,
                CLIP,
                FEATURETTE
        })
        public @interface Def {}

        public static @Def String transformToType(final String type) {
            switch (type) {
                case VideoLink.Type.TRAILER:
                    return VideoLink.Type.TRAILER;
                case VideoLink.Type.TEASER:
                    return VideoLink.Type.TEASER;
                case VideoLink.Type.CLIP:
                    return VideoLink.Type.CLIP;
                case VideoLink.Type.FEATURETTE:
                    return VideoLink.Type.FEATURETTE;
                default:
                    throw new IllegalArgumentException("Unknown type");
            }
        }
    }

    private final String mID;
    private final String mKey;
    private final String mName;
    private final String mSite;
    private final int mSize;
    private final @Type.Def String mType;

    /**
     *
     * @param id
     * @param key
     * @param name
     * @param site
     * @param size
     * @param type
     */
    public VideoLink(final String id,
                     final String key,
                     final String name,
                     final String site,
                     final int size,
                     final @Type.Def String type) {

        Preconditions.checkNotNull(id, "id must not be null.");
        Preconditions.checkNotNull(key, "key must not be null.");
        Preconditions.checkNotNull(name, "name must not be null.");
        Preconditions.checkNotNull(site, "site must not be null.");
        Preconditions.checkArgument(size > 0, "size must be a positive integer.");
        Preconditions.checkNotNull(type, "type must not be null.");

        this.mID = id;
        this.mKey = key;
        this.mName = name;
        this.mSite = site;
        this.mSize = size;
        this.mType = type;
    }

    /**
     *
     * @return
     */
    public String getID() {
        return mID;
    }

    /**
     *
     * @return
     */
    public String getKey() {
        return mKey;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return mName;
    }

    /**
     *
     * @return
     */
    public String getSite() {
        return mSite;
    }

    /**
     *
     * @return
     */
    public int getSize() {
        return mSize;
    }

    /**
     *
     * @return
     */
    public @Type.Def String getType() {
        return mType;
    }

    @Override
    public String toString() {
        return "[ ID=" + mID +
                ", Key=" + mKey +
                ", Name=" + mName +
                ", Site=" + mSite +
                ", Size=" + mSize +
                ", Type=" + mType +
                " ]";
    }
}
