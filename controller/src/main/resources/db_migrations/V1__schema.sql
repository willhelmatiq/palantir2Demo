DROP TABLE IF EXISTS videos;
CREATE TABLE videos
(
    video_id                 varchar(11) PRIMARY KEY  NOT NULL,
    channel_id               varchar(24)              NOT NULL,
    published                timestamp with time zone NOT NULL,
    last_metadata_indexation timestamp with time zone,
    last_stats_indexation    timestamp with time zone,
    row_created              timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    row_updated              timestamp with time zone
);

DROP TABLE IF EXISTS channels;
CREATE TABLE channels
(
    channel_id      varchar(24) PRIMARY KEY  NOT NULL,
    last_indexation timestamp with time zone NOT NULL,
    videos_count    integer,
    row_created     timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    row_updated     timestamp with time zone
);

DROP TABLE IF EXISTS watched_channels_queue;
CREATE TABLE watched_channels_queue
(
    id          bigserial PRIMARY KEY,
    channel_id  varchar(24)                       default NULL,
    custom_url  varchar                           default NULL,
    user_name   varchar                           default NULL,
    is_watched  bool                              default FALSE,
    is_invalid  bool                              default FALSE,
    retry_count int                               default 0,
    row_created timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    row_updated timestamp with time zone
);
