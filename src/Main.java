import java.util.Random;
import java.util.Scanner;

public class Main {
    public static char[][] matrix;
    public static Random ran = new Random();
    public static final char GUY = '@';
    public static int ROWGUY = 0;
    public static int COLGUY = 0;

    public static final char EXIT = 'E';
    public static final char STONE = 'S';
    public static int ROWSTONE = 0;
    public static int COLSTONE = 0;

    public static final char BUSH = '*'; // храст

    public static boolean isGameOver = false;
    public static boolean youWin = false;
    public static int LEVEL;
    public static int SIZE;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        startKit(scanner);

        while (!isGameOver && !youWin) {
            System.out.println("Please enter your command:");
            String command = scanner.next();

            if (!"wsad".contains(command)) {
                System.out.println("Your command is invalid. Please enter next");
                continue;
            }

            checkCommand(command.charAt(0), ROWGUY, COLGUY);

            printMatrix();
        }

        if (isGameOver) {
            System.out.println("GAME OVER");
        } else {
            System.out.println("YOU WIN");
        }

    }

    private static void startKit(Scanner scanner) {
        System.out.println("Hello, your goal is to move the stone to E point.");

        System.out.println("Please enter level: \n 1 for easy\n 2 for medium \n 3 for hard");

        do {
            LEVEL = scanner.nextInt();
        } while (LEVEL < 0 || LEVEL > 3);

        createLevel();

        System.out.println("Your position is: @");
        System.out.println("The stone position is: S");
        System.out.println("The bushes are: *");

        System.out.println("You can move : \n s - down \n w - up \n a - left \n d - right");
        System.out.println();

        matrix = new char[SIZE][SIZE];

        fillMatrix();

        matrix[matrix.length - 1][matrix.length - 1] = EXIT;

        createBush();
        currentPlace(GUY);
        currentPlace(STONE);

        printMatrix();
    }

    private static void checkCommand(char command, int row, int col) {
        int currentRow = row;
        int currentCol = col;

        int nextRow = currentRow;
        int nextCol = currentCol;

        switch (command) {
            case 's':
                currentRow++;
                nextRow = currentRow + 1;
                break;
            case 'w':
                currentRow--;
                nextRow = currentRow - 1;
                break;
            case 'a':
                currentCol--;
                nextCol = currentCol - 1;
                break;
            case 'd':
                currentCol++;
                nextCol = currentCol + 1;
                break;
        }

        if (checkBorder(currentRow, currentCol)) { // ако следващата клетка е в граница
            switch (matrix[currentRow][currentCol]) {
                case ' ':                //следващата клетка е свободна
                    matrix[row][col] = ' ';
                        ROWGUY = currentRow;
                        COLGUY = currentCol;
                        matrix[ROWGUY][COLGUY] = GUY;
                    break;
                case STONE:
                    // дали пред STONE клетката е в граница
                    if (checkBorder(nextRow, nextCol)) {
                        switch (matrix[nextRow][nextCol]) {
                            case ' ':
                                ROWSTONE = nextRow;
                                COLSTONE = nextCol;

                                checkPointStone(ROWSTONE, COLSTONE); //проверки за клетката пред камъка

                                matrix[ROWSTONE][COLSTONE] = STONE;

                                checkSpecificPoint(ROWSTONE, COLSTONE);

                                matrix[row][col] = ' ';
                                    ROWGUY = currentRow;
                                    COLGUY = currentCol;
                                    matrix[ROWGUY][COLGUY] = GUY;
                                break;
                            case 'E':
                                youWin = true;
                                break;
                        }
                    }
                    break;
//                case EXIT: // exit
//                    isGameOver = true;
//                    break;
            }
        }
    }

    private static void checkSpecificPoint(int row, int col) {
        if ((row == matrix.length - 1 &&
                matrix[matrix.length - 1][col - 1] == BUSH)
                || (col == matrix.length - 1 &&
                matrix[row - 1][matrix.length - 1] == BUSH)) {
            isGameOver = true;
        }

    }

    private static boolean checkBorder(int row, int col) {
        if (row < 0 || col < 0 || row >= matrix.length || col >= matrix[0].length) {
            return false;
        }
        return true;
    }

    private static void checkPointStone(int row, int col) {
        if (hasPath(row, col)) {
            if (row == 0 || col == 0) {
                isGameOver = true;
            } else if (row == matrix.length - 1) {
                checkForBushRow(row, col);
            } else if (col == matrix.length - 1) {
                checkForBushCol(row, col);
            } else if (matrix[row][col - 1] == BUSH && matrix[row - 1][col] == BUSH
                    || matrix[row - 1][col] == BUSH && matrix[row][col + 1] == BUSH
                    || matrix[row][col + 1] == BUSH && matrix[row + 1][col] == BUSH
                    || matrix[row + 1][col] == BUSH && matrix[row][col - 1] == BUSH) {
                isGameOver = true;
            }
        } else {
            isGameOver = true;
        }

        checkSpecificPoint(row, col);

        // clean matrix from checking
        cleanMatrix(matrix);
    }

    private static void cleanMatrix(char[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] == '!') {
                    matrix[i][j] = ' ';
                }
            }
        }
    }

    private static boolean hasPath(int row, int col) {
        if (row < 0 || row == matrix.length || col < 0 || col == matrix[0].length) {
            return false;
        }

        if (matrix[row][col] == BUSH || matrix[row][col] == GUY
                || matrix[row][col] == '!' || matrix[row][col] == STONE) {
            return false;
        }

        if (matrix[row][col] == EXIT) {
            return true;
        }

        matrix[row][col] = '!';

        return hasPath(row + 1, col) ||
                hasPath(row - 1, col) ||
                hasPath(row, col + 1) ||
                hasPath(row, col - 1);


    }

    private static void checkForBushCol(int row, int col) {
        for (int i = row + 1; i < matrix.length; i++) {
            if (matrix[i][col] == BUSH) {
                isGameOver = true;
                break;
            }
        }
    }

    private static void checkForBushRow(int row, int col) {
        for (int i = col + 1; i < matrix.length; i++) {
            if (matrix[row][i] == BUSH) {
                isGameOver = true;
                break;
            }
        }
    }

    private static void fillMatrix() {
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                matrix[row][col] = ' '; // 32
            }
        }
    }

    public static void createLevel() {

        switch (LEVEL) {
            case 1:
                LEVEL = 5; // 5 ATTENTION ATTENTION ATTENTION ATTENTION
                SIZE = 20;
                break;
            case 2:
                LEVEL = 10;
                SIZE = 40;
                break;
            case 3:
                LEVEL = 15;
                SIZE = 100;
                break;
        }
    }

    private static void createBush() {
        int percent = (LEVEL * matrix.length * matrix.length) / 100;
        for (int i = 0; i < percent; i++) {
            currentPlace(BUSH);
        }
    }

    private static void currentPlace(char sign) {
        int row;
        int col;

        do {
            row = ran.nextInt(matrix.length);
            col = ran.nextInt(matrix.length);
        } while (matrix[row][col] != ' ');

        if (sign == STONE) {
            checkPointStone(row, col);
            matrix[row][col] = sign;
            ROWSTONE = row;
            COLSTONE = col;
        } else if (sign == GUY) {
            matrix[row][col] = sign;
            ROWGUY = row;
            COLGUY = col;
        } else {
            matrix[row][col] = sign;
        }
    }

    private static void printMatrix() { // print map
        for (int i = 0; i < matrix.length + 2; i++) {
            System.out.print("_");
        }
        System.out.println();

        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                if (col == 0) {
                    System.out.print("|");
                    System.out.print(matrix[row][col]);
                } else if (col == matrix[row].length - 1) {
                    System.out.print(matrix[row][col]);
                    System.out.print("|");
                } else {
                    System.out.print(matrix[row][col]);
                }
            }
            System.out.println();
        }

        for (int i = 0; i < matrix.length + 2; i++) {
            System.out.print("-");
        }
        System.out.println();
    }


}
