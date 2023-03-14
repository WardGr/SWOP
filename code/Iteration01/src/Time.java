public class Time implements Comparable<Time> {

  private final int hour;
  private final int minute;

  public Time(int hour, int minute) throws InvalidTimeException {
    if (minute < 0 || minute > 59) {
      throw new InvalidTimeException();
    }
    this.hour = hour;
    this.minute = minute;
  }

  public int getTotalMinutes() {
    return getHour() * 60 + getMinute();
  }

  public Time(int totalMinutes) {
    this.hour = totalMinutes / 60;
    this.minute = totalMinutes % 60;
  }


  public int getHour() {
    return hour;
  }

  public int getMinute() {
    return minute;
  }

  public boolean before(Time time) {
    return this.compareTo(time) < 0;
  }

  public boolean after(Time time) {
    return this.compareTo(time) > 0;
  }

  @Override
  public String toString() {
    return String.format("%02d", getHour()) + ":" + String.format("%02d", getMinute());
  }

  @Override
  public int compareTo(Time other) {
    if (getHour() == other.getHour()) {
      if (getMinute() == other.getMinute()) {
        return 0;
      } else if (getMinute() < other.getMinute()) {
        return -1;
      }
      return 1;
    }
    if (getHour() < other.getHour()) {
      return -1;
    }
    return 1;
  }

  public Time subtract(Time startTime) {
    return new Time(this.getTotalMinutes() - startTime.getTotalMinutes());
  }
  /*public static Time difference(Time time1, Time time2){
        if (time1.compareTo(time2) < 0){
            return new Time(0,0);
        }
        int hours = time1.getHour() - time2.getHour();
        int minutes;
        if (time1.getMinute() < time2.getMinute()){
            hours -= 1;
            minutes = 60 - (time1.getMinute() - time2.getMinute());
        } else {
            minutes = time1.getMinute() - time2.getMinute();
        }
        return new Time(hours,minutes);
    }*/
}
