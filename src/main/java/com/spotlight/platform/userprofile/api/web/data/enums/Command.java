package com.spotlight.platform.userprofile.api.web.data.enums;

public enum Command {
    COLLECT("collect"),
    INCREMENT("increment"),
    REPLACE("replace");

    private final String type;

    Command(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static Command fromType(String type) {
        for (Command command : values()) {
            if (command.type.equals(type)) {
                return command;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + type + "]");
    }
}
