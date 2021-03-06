# Тестовое задание для стажировки в "IntelliJ plugin for Lego Mindstorms"

У вас есть Java функция в которую пользователь передает текст и регулярное выражение. Измените
функцию так, чтобы избежать зависаний и выбрасывания исключений в процессе исполнения.

```
public boolean matches(String text, String regex) {
    return Pattern.compile(regex).matcher(text).matches();
}
```

*Подсказка: вы не хотите вечно ждать пока matches() закончит работу.*

## [Решение](MatchesProblem.java)

Стоит отметить, что задание можно трактовать неоднозначно. В процессе изучения проблемы, я пришел к
двум возможным трактовкам:

1. Мы не хотим, чтобы `matches` работал больше, чем какое-то кол-во времени и это не сказывалось 
   на работе основного приложения. Нужно запустить функцию в отдельном треде на определенное 
   время и остановить проверку без выбрасывания исключений и завершения работы всей программы
   
2. У регулярок есть большое кол-во проблем, когда плохо составленное выражение может занимать 
   неоправданное время для проверки и тд. Задание могло подразумевать парсинг и изменение 
   выражение, но мне показалось, что это размышление неверно, поэтому здесь приведено решение 
   первой проблемы. \
   Подробнее про различные подводные камни можно почитать например 
   [здесь](http://www.regular-expressions.info/catastrophic.html)
   
### Работа метода:
* `Pattern.compile` - компилим паттерн, если выкинутся ошибки, вернем `false`
* Берем `Matcher` из скомпиленного паттерна
* Создаем отдельный один тред с `matches` с помощью `ExecutorService`
* Запускаем полученный `FutureResult` с таймаутом
* Если пробросилась ошибка, в том числе об истечении времени, метод вернет `false`
* Не забудем закрыть `ExecutorService` и остановить `FutureResult`