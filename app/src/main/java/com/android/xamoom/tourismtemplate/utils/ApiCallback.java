package com.android.xamoom.tourismtemplate.utils;

public interface ApiCallback {
  interface ObjectCallback<T, E> {
    void finish(T result, E error);
  }

  interface ListCallback<T> {
    void finish(T result, String cursor, Boolean hasMore);
  }
}
