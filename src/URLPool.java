import java.util.LinkedList;

public class URLPool {
  int maxDepth;
  int count_waitThread; // Счётчик, который хранит в себе значение количества потоков ожидания нового URL-адреса

  LinkedList<URLDepthPair> not_viewed_url = new LinkedList<>(); // Список для непросмотренных ссылок
  LinkedList<URLDepthPair> viewed_url = new LinkedList<>(); // Список для просмотреных ссылок

  public URLPool(int maxDepth) { // Конструктор класса
    this.maxDepth = maxDepth;
    count_waitThread = 0;
  }

  public synchronized URLDepthPair getPair() {
    while (not_viewed_url.size() == 0) { // Если ни один адрес в настоящее время недоступен
      count_waitThread++; // Увеличивается непосредственно перед вызовом wait()
      try {
        wait();
      } catch (InterruptedException e) {
        System.out.println("InterruptedException");
      }
      count_waitThread--; // Уменьшается сразу после выхода из режима ожидания
    }

    return not_viewed_url.removeFirst(); // получение и удаление адреса из списка
  }

  // Поточно-ориентированная операция добавления пары URL-глубина к пулу непросмотренных URL-адресов
  public synchronized void addPair(URLDepthPair pair) {
    if (viewed_url.contains(pair) == false) { // Если просмотренные ссылки не содержат пару URL-глубины
      viewed_url.add(pair); // то добавить эту пару к пулу
      if (pair.get_depth() < maxDepth) { // если глубина пары URL-глубины меньше максимальной глубины поиска
        not_viewed_url.add(pair); // то добавить эту пару к непросмотренным ссылкам
        notify(); // Продолжает работу потока, у которого ранее был вызван метод wait()
      }
    }
  }

  public synchronized int getWait() {
    return count_waitThread;
  }

  public LinkedList<URLDepthPair> getChecked() {
    return viewed_url;
  }
}