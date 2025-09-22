package com.mipt.alexandervolchenko;

abstract class Workers {
  public abstract void work(int n);

  public boolean goHome(String n1, String n2){
    return n1.equals(n2);
  }
}
