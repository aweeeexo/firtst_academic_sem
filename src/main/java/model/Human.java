package model;

public class Human {
  private String name;
  private String surname;
  private int age;
  private boolean isHeWork;

  public void SetName(String name){
    this.name = name;
  }

  public void SetSurname(String surname){
    this.surname = surname;
  }

  public void SetAge(int age){
    this.age = age;
  }

  public void SetIsHeWork(boolean isHeWork){
    this.isHeWork = isHeWork;
  }

  public String getName(){
    return this.name;
  }

  public String getSurname(){
    return this.surname;
  }

  public int getAge(){
    return this.age;
  }

  public boolean getIsHeWork(){
    return this.isHeWork;
  }
}