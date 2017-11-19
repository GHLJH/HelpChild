package com.le.help_child.bean;

import java.util.List;

/**
 * Created on 2017/5/23.
 */

public class MessageListBean {

    /**
     * tel :
     * message :
     * datetime : 201705210337
     */

    private List<NoteBean> note;

    public List<NoteBean> getNote() {
        return note;
    }

    public void setNote(List<NoteBean> note) {
        this.note = note;
    }

    public static class NoteBean {
        private String tel;
        private String message;
        private String datetime;

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }
    }
}
