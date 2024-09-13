import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class LoanPaymentCalculator {

    //Праздники
    private static final List<String> holidays = List.of(
            "01-01",  // Новый Год
            "05-01",  // День Труда
            "02-23",  // 23 Февраля
            "03-08"  // 8 Марта
    );

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ввод данных
        System.out.print("Сумму кредита: ");
        double loanAmount = scanner.nextDouble();

        System.out.print("Срок кредита (в месяцах): ");
        int loanTerm = scanner.nextInt();

        System.out.print("Процентную ставку (%): ");
        double annualInterestRate = scanner.nextDouble();

        System.out.print("Дату выдачи кредита (день месяца 1-31): ");
        int issueDay = scanner.nextInt();

        System.out.print("Введите тип графика (аннуитетный - 1, дифференцированный - 2): ");
        int paymentType = scanner.nextInt();

        System.out.print("Введите дату выдачи кредита (год-месяц-день): ");
        String issueDateString = scanner.next();
        LocalDate issueDate = LocalDate.parse(issueDateString, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Расчет графика платежей
        if (paymentType == 1) {
            calculateAnnuityPayments(loanAmount, loanTerm, annualInterestRate, issueDay, issueDate);
        } else if (paymentType == 2) {
            calculateDifferentiatedPayments(loanAmount, loanTerm, annualInterestRate, issueDay, issueDate);
        } else {
            System.out.println("Неизвестный тип графика.");
        }

        scanner.close();
    }

    // Коррекция даты платежа (если дата выпадает на выходной или праздник)
    private static LocalDate adjustPaymentDate(LocalDate paymentDate) {
        // Преобразуем дату в формат "MM-dd"
        String paymentDay = paymentDate.format(DateTimeFormatter.ofPattern("MM-dd"));

        // Проверяем, является ли дата выходным или праздником
        while (paymentDate.getDayOfWeek() == DayOfWeek.SUNDAY || holidays.contains(paymentDay)) {
            paymentDate = paymentDate.plusDays(1);
            paymentDay = paymentDate.format(DateTimeFormatter.ofPattern("MM-dd"));
        }
        return paymentDate;
    }

    // Расчет аннуитетных платежей
    private static void calculateAnnuityPayments(double loanAmount, int loanTerm, double annualInterestRate, int issueDay, LocalDate issueDate) {
        // Месячная процентная ставка
        double monthlyInterestRate = annualInterestRate / 12 / 100;

        // Формула для аннуитетного платежа
        double annuityPayment = loanAmount * (monthlyInterestRate / (1 - Math.pow(1 + monthlyInterestRate, -loanTerm)));

        System.out.println("\nАннуитетные платежи:");
        System.out.printf("%-10s%-15s%-15s%n", "Платеж №", "Дата", "Сумма");
        System.out.println("--------------------------------------");

        LocalDate paymentDate = issueDate.withDayOfMonth(issueDay);

        for (int month = 1; month <= loanTerm; month++) {
            paymentDate = paymentDate.plusMonths(1);
            paymentDate = adjustPaymentDate(paymentDate);

            System.out.printf("%-10d%-15s%-15.2f%n", month, paymentDate, annuityPayment);
        }
    }

    // Расчет дифференцированных платежей
    private static void calculateDifferentiatedPayments(double loanAmount, int loanTerm, double annualInterestRate, int issueDay, LocalDate issueDate) {
        double monthlyInterestRate = annualInterestRate / 12 / 100;
        double principalPayment = loanAmount / loanTerm;

        System.out.println("\nДифференцированные платежи:");
        System.out.printf("%-10s%-15s%-15s%n", "Платеж №", "Дата", "Сумма");
        System.out.println("--------------------------------------");

        LocalDate paymentDate = issueDate.withDayOfMonth(issueDay);

        for (int month = 1; month <= loanTerm; month++) {
            paymentDate = paymentDate.plusMonths(1);
            paymentDate = adjustPaymentDate(paymentDate);

            double interestPayment = (loanAmount - principalPayment * (month - 1)) * monthlyInterestRate;
            double totalPayment = principalPayment + interestPayment;

            System.out.printf("%-10d%-15s%-15.2f%n", month, paymentDate, totalPayment);
        }
    }
}
