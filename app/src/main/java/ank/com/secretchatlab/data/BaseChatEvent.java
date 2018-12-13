package ank.com.secretchatlab.data;

import ank.com.secretchatlab.data.entity.BaseEntity;

public class BaseChatEvent {

    private BaseEntity entity;

    public BaseChatEvent(BaseEntity entity) {
        this.entity = entity;
    }

    public BaseEntity getEntity() {
        return entity;
    }
}
