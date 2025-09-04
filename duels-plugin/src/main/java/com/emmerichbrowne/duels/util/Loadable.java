package com.emmerichbrowne.duels.util;

public interface Loadable {

    void handleLoad() throws Exception;

    void handleUnload() throws Exception;
}
