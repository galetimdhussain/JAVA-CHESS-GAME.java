/*Java Chess Game Application Full Source Code ChessBoard:*/

public class ChessBoard {
  private Piece[][] board;

  public ChessBoard() {
      this.board = new Piece[8][8]; // Chessboard is 8x8
      setupPieces();
  }

  public Piece[][] getBoard() {
      return board;
  }

  public Piece getPiece(int row, int column) {
      return board[row][column];
  }

  public void setPiece(int row, int column, Piece piece) {
      board[row][column] = piece;
      if (piece != null) {
          piece.setPosition(new Position(row, column));
      }
  }

  private void setupPieces() {
      // Place Rooks
      board[0][0] = new Rook(PieceColor.BLACK, new Position(0, 0));
      board[0][7] = new Rook(PieceColor.BLACK, new Position(0, 7));
      board[7][0] = new Rook(PieceColor.WHITE, new Position(7, 0));
      board[7][7] = new Rook(PieceColor.WHITE, new Position(7, 7));
      // Place Knights
      board[0][1] = new Knight(PieceColor.BLACK, new Position(0, 1));
      board[0][6] = new Knight(PieceColor.BLACK, new Position(0, 6));
      board[7][1] = new Knight(PieceColor.WHITE, new Position(7, 1));
      board[7][6] = new Knight(PieceColor.WHITE, new Position(7, 6));
      // Place Bishops
      board[0][2] = new Bishop(PieceColor.BLACK, new Position(0, 2));
      board[0][5] = new Bishop(PieceColor.BLACK, new Position(0, 5));
      board[7][2] = new Bishop(PieceColor.WHITE, new Position(7, 2));
      board[7][5] = new Bishop(PieceColor.WHITE, new Position(7, 5));
      // Place Queens
      board[0][3] = new Queen(PieceColor.BLACK, new Position(0, 3));
      board[7][3] = new Queen(PieceColor.WHITE, new Position(7, 3));
      // Place Kings
      board[0][4] = new King(PieceColor.BLACK, new Position(0, 4));
      board[7][4] = new King(PieceColor.WHITE, new Position(7, 4));
      // Place Pawns
      for (int i = 0; i < 8; i++) {
          board[1][i] = new Pawn(PieceColor.BLACK, new Position(1, i));
          board[6][i] = new Pawn(PieceColor.WHITE, new Position(6, i));
      }
  }

  public void movePiece(Position start, Position end) {
      if (board[start.getRow()][start.getColumn()] != null &&
              board[start.getRow()][start.getColumn()].isValidMove(end, board)) {

          board[end.getRow()][end.getColumn()] = board[start.getRow()][start.getColumn()];
          board[end.getRow()][end.getColumn()].setPosition(end);
          board[start.getRow()][start.getColumn()] = null;
      }
  }
}



ChessGame:

import java.util.List;
import java.util.ArrayList;

public class ChessGame {
  private ChessBoard board;
  private boolean whiteTurn = true; // White starts the game

  public ChessGame() {
      this.board = new ChessBoard();
  }

  public ChessBoard getBoard() {
      return this.board;
  }

  public void resetGame() {
      this.board = new ChessBoard();
      this.whiteTurn = true;
  }

  public PieceColor getCurrentPlayerColor() {
      return whiteTurn ? PieceColor.WHITE : PieceColor.BLACK;
  }

  private Position selectedPosition;

  public boolean isPieceSelected() {
      return selectedPosition != null;
  }

  public boolean handleSquareSelection(int row, int col) {
      if (selectedPosition == null) {
          Piece selectedPiece = board.getPiece(row, col);
          if (selectedPiece != null
                  && selectedPiece.getColor() == (whiteTurn ? PieceColor.WHITE : PieceColor.BLACK)) {
              selectedPosition = new Position(row, col);
              return false;
          }
      } else {
          boolean moveMade = makeMove(selectedPosition, new Position(row, col));
          selectedPosition = null;
          return moveMade;
      }
      return false;
  }

  public boolean makeMove(Position start, Position end) {
      Piece movingPiece = board.getPiece(start.getRow(), start.getColumn());
      if (movingPiece == null || movingPiece.getColor() != (whiteTurn ? PieceColor.WHITE : PieceColor.BLACK)) {
          return false;
      }

      if (movingPiece.isValidMove(end, board.getBoard())) {
          board.movePiece(start, end);
          whiteTurn = !whiteTurn;
          return true;
      }
      return false;
  }

  public boolean isInCheck(PieceColor kingColor) {
      Position kingPosition = findKingPosition(kingColor);
      for (int row = 0; row < board.getBoard().length; row++) {
          for (int col = 0; col < board.getBoard()[row].length; col++) {
              Piece piece = board.getPiece(row, col);
              if (piece != null && piece.getColor() != kingColor) {
                  if (piece.isValidMove(kingPosition, board.getBoard())) {
                      return true;
                  }
              }
          }
      }
      return false;
  }

  private Position findKingPosition(PieceColor color) {
      for (int row = 0; row < board.getBoard().length; row++) {
          for (int col = 0; col < board.getBoard()[row].length; col++) {
              Piece piece = board.getPiece(row, col);
              if (piece instanceof King && piece.getColor() == color) {
                  return new Position(row, col);
              }
          }
      }
      throw new RuntimeException("King not found, which should never happen.");
  }

  public boolean isCheckmate(PieceColor kingColor) {
      if (!isInCheck(kingColor)) {
          return false;
      }

      Position kingPosition = findKingPosition(kingColor);
      King king = (King) board.getPiece(kingPosition.getRow(), kingPosition.getColumn());

      for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
          for (int colOffset = -1; colOffset <= 1; colOffset++) {
              if (rowOffset == 0 && colOffset == 0) {
                  continue;
              }
              Position newPosition = new Position(kingPosition.getRow() + rowOffset,
                      kingPosition.getColumn() + colOffset);

              if (isPositionOnBoard(newPosition) && king.isValidMove(newPosition, board.getBoard())
                      && !wouldBeInCheckAfterMove(kingColor, kingPosition, newPosition)) {
                  return false;
              }
          }
      }
      return true;
  }

  private boolean isPositionOnBoard(Position position) {
      return position.getRow() >= 0 && position.getRow() < board.getBoard().length &&
              position.getColumn() >= 0 && position.getColumn() < board.getBoard()[0].length;
  }

  private boolean wouldBeInCheckAfterMove(PieceColor kingColor, Position from, Position to) {
      Piece temp = board.getPiece(to.getRow(), to.getColumn());
      board.setPiece(to.getRow(), to.getColumn(), board.getPiece(from.getRow(), from.getColumn()));
      board.setPiece(from.getRow(), from.getColumn(), null);

      boolean inCheck = isInCheck(kingColor);

      board.setPiece(from.getRow(), from.getColumn(), board.getPiece(to.getRow(), to.getColumn()));
      board.setPiece(to.getRow(), to.getColumn(), temp);

      return inCheck;
  }

  public List<Position> getLegalMovesForPieceAt(Position position) {
      Piece selectedPiece = board.getPiece(position.getRow(), position.getColumn());
      if (selectedPiece == null)
          return new ArrayList<>();

      List<Position> legalMoves = new ArrayList<>();
      switch (selectedPiece.getClass().getSimpleName()) {
          case "Pawn":
              addPawnMoves(position, selectedPiece.getColor(), legalMoves);
              break;
          case "Rook":
              addLineMoves(position, new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 } }, legalMoves);
              break;
          case "Knight":
              addSingleMoves(position, new int[][] { { 2, 1 }, { 2, -1 }, { -2, 1 }, { -2, -1 }, { 1, 2 }, { -1, 2 },
                      { 1, -2 }, { -1, -2 } }, legalMoves);
              break;
          case "Bishop":
              addLineMoves(position, new int[][] { { 1, 1 }, { -1, -1 }, { 1, -1 }, { -1, 1 } }, legalMoves);
              break;
          case "Queen":
              addLineMoves(position, new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { -1, -1 },
                      { 1, -1 }, { -1, 1 } }, legalMoves);
              break;
          case "King":
              addSingleMoves(position, new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { -1, -1 },
                      { 1, -1 }, { -1, 1 } }, legalMoves);
              break;
      }
      return legalMoves;
  }

  private void addLineMoves(Position position, int[][] directions, List<Position> legalMoves) {
      for (int[] d : directions) {
          Position newPos = new Position(position.getRow() + d[0], position.getColumn() + d[1]);
          while (isPositionOnBoard(newPos)) {
              if (board.getPiece(newPos.getRow(), newPos.getColumn()) == null) {
                  legalMoves.add(new Position(newPos.getRow(), newPos.getColumn()));
                  newPos = new Position(newPos.getRow() + d[0], newPos.getColumn() + d[1]);
              } else {
                  if (board.getPiece(newPos.getRow(), newPos.getColumn()).getColor() != board
                          .getPiece(position.getRow(), position.getColumn()).getColor()) {
                      legalMoves.add(newPos);
                  }
                  break;
              }
          }
      }
  }

  private void addSingleMoves(Position position, int[][] moves, List<Position> legalMoves) {
      for (int[] move : moves) {
          Position newPos = new Position(position.getRow() + move[0], position.getColumn() + move[1]);
          if (isPositionOnBoard(newPos) && (board.getPiece(newPos.getRow(), newPos.getColumn()) == null ||
                  board.getPiece(newPos.getRow(), newPos.getColumn()).getColor() != board
                          .getPiece(position.getRow(), position.getColumn()).getColor())) {
              legalMoves.add(newPos);
          }
      }
  }

  private void addPawnMoves(Position position, PieceColor color, List<Position> legalMoves) {
      int direction = color == PieceColor.WHITE ? -1 : 1;
      Position newPos = new Position(position.getRow() + direction, position.getColumn());
      if (isPositionOnBoard(newPos) && board.getPiece(newPos.getRow(), newPos.getColumn()) == null) {
          legalMoves.add(newPos);
      }

      if ((color == PieceColor.WHITE && position.getRow() == 6)
              || (color == PieceColor.BLACK && position.getRow() == 1)) {
          newPos = new Position(position.getRow() + 2 * direction, position.getColumn());
          Position intermediatePos = new Position(position.getRow() + direction, position.getColumn());
          if (isPositionOnBoard(newPos) && board.getPiece(newPos.getRow(), newPos.getColumn()) == null
                  && board.getPiece(intermediatePos.getRow(), intermediatePos.getColumn()) == null) {
              legalMoves.add(newPos);
          }
      }

      int[] captureCols = { position.getColumn() - 1, position.getColumn() + 1 };
      for (int col : captureCols) {
          newPos = new Position(position.getRow() + direction, col);
          if (isPositionOnBoard(newPos) && board.getPiece(newPos.getRow(), newPos.getColumn()) != null &&
                  board.getPiece(newPos.getRow(), newPos.getColumn()).getColor() != color) {
              legalMoves.add(newPos);
          }
      }
  }
}
ChessGameGui:

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.*;

public class ChessGameGUI extends JFrame {
  private final ChessSquareComponent[][] squares = new ChessSquareComponent[8][8];
  private final ChessGame game = new ChessGame();

  private final Map<Class<? extends Piece>, String> pieceUnicodeMap = new HashMap<>() {
      {
          put(Pawn.class, "\u265F");
          put(Rook.class, "\u265C");
          put(Knight.class, "\u265E");
          put(Bishop.class, "\u265D");
          put(Queen.class, "\u265B");
          put(King.class, "\u265A");
      }
  };

  public ChessGameGUI() {
      setTitle("Chess Game");
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setLayout(new GridLayout(8, 8));
      initializeBoard();
      addGameResetOption();
      pack();
      setVisible(true);
  }

  private void initializeBoard() {
      for (int row = 0; row < squares.length; row++) {
          for (int col = 0; col < squares[row].length; col++) {
              final int finalRow = row;
              final int finalCol = col;
              ChessSquareComponent square = new ChessSquareComponent(row, col);
              square.addMouseListener(new MouseAdapter() {
                  @Override
                  public void mouseClicked(MouseEvent e) {
                      handleSquareClick(finalRow, finalCol);
                  }
              });
              add(square);
              squares[row][col] = square;
          }
      }
      refreshBoard();
  }

  private void refreshBoard() {
      ChessBoard board = game.getBoard();
      for (int row = 0; row < 8; row++) {
          for (int col = 0; col < 8; col++) {
              Piece piece = board.getPiece(row, col);
              if (piece != null) {
                  // If using Unicode symbols:
                  String symbol = pieceUnicodeMap.get(piece.getClass());
                  Color color = (piece.getColor() == PieceColor.WHITE) ? Color.WHITE : Color.BLACK;
                  squares[row][col].setPieceSymbol(symbol, color);
              } else {
                  squares[row][col].clearPieceSymbol();
              }
          }
      }
  }

  private void handleSquareClick(int row, int col) {
      boolean moveResult = game.handleSquareSelection(row, col);
      clearHighlights();
      if (moveResult) {
          refreshBoard();
          checkGameState();
          checkGameOver();
      } else if (game.isPieceSelected()) {
          highlightLegalMoves(new Position(row, col));
      }
      refreshBoard();
  }

  private void checkGameState() {
      PieceColor currentPlayer = game.getCurrentPlayerColor();
      boolean inCheck = game.isInCheck(currentPlayer);

      if (inCheck) {
          JOptionPane.showMessageDialog(this, currentPlayer + " is in check!");
      }
  }

  private void highlightLegalMoves(Position position) {
      List<Position> legalMoves = game.getLegalMovesForPieceAt(position);
      for (Position move : legalMoves) {
          squares[move.getRow()][move.getColumn()].setBackground(Color.GREEN);
      }
  }

  private void clearHighlights() {
      for (int row = 0; row < 8; row++) {
          for (int col = 0; col < 8; col++) {
              squares[row][col].setBackground((row + col) % 2 == 0 ? Color.LIGHT_GRAY : new Color(205, 133, 63));
          }
      }
  }

  private void addGameResetOption() {
      JMenuBar menuBar = new JMenuBar();
      JMenu gameMenu = new JMenu("Game");
      JMenuItem resetItem = new JMenuItem("Reset");
      resetItem.addActionListener(e -> resetGame());
      gameMenu.add(resetItem);
      menuBar.add(gameMenu);
      setJMenuBar(menuBar);
  }

  private void resetGame() {
      game.resetGame();
      refreshBoard();
  }

  private void checkGameOver() {
      if (game.isCheckmate(game.getCurrentPlayerColor())) {
          int response = JOptionPane.showConfirmDialog(this, "Checkmate! Would you like to play again?", "Game Over",
                  JOptionPane.YES_NO_OPTION);
          if (response == JOptionPane.YES_OPTION) {
              resetGame();
          } else {
              System.exit(0);
          }
      }
  }

  public static void main(String[] args) {
      SwingUtilities.invokeLater(ChessGameGUI::new);
  }
}
ChessSquareComponent:

import javax.swing.*;
import java.awt.*;

public class ChessSquareComponent extends JButton {
  private int row;
  private int col;

  public ChessSquareComponent(int row, int col) {
      this.row = row;
      this.col = col;
      initButton();
  }

  private void initButton() {
      setPreferredSize(new Dimension(64, 64));

      if ((row + col) % 2 == 0) {
          setBackground(Color.LIGHT_GRAY);
      } else {
          setBackground(new Color(205, 133, 63));
      }

      setHorizontalAlignment(SwingConstants.CENTER);
      setVerticalAlignment(SwingConstants.CENTER);
      setFont(new Font("Serif", Font.BOLD, 36));
  }

  public void setPieceSymbol(String symbol, Color color) {
      this.setText(symbol);
      this.setForeground(color);
  }

  public void clearPieceSymbol() {
      this.setText("");
  }
}
Position:

public class Position {
  private int row;
  private int column;

  public Position(int row, int column) {
      this.row = row;
      this.column = column;
  }

  public int getRow() {
      return row;
  }

  public int getColumn() {
      return column;
  }
}
Piece:

public abstract class Piece {
  protected Position position;
  protected PieceColor color;

  public Piece(PieceColor color, Position position) {
      this.color = color;
      this.position = position;
  }

  public PieceColor getColor() {
      return color;
  }

  public Position getPosition() {
      return position;
  }

  public void setPosition(Position position) {
      this.position = position;
  }

  public abstract boolean isValidMove(Position newPosition, Piece[][] board);
}
PieceColor:

public enum PieceColor {
  BLACK, WHITE;
}
Pawn:

public class Pawn extends Piece {
  public Pawn(PieceColor color, Position position) {
      super(color, position);
  }

  @Override
  public boolean isValidMove(Position newPosition, Piece[][] board) {
      int forwardDirection = color == PieceColor.WHITE ? -1 : 1;
      int rowDiff = (newPosition.getRow() - position.getRow()) * forwardDirection;
      int colDiff = newPosition.getColumn() - position.getColumn();

      if (colDiff == 0 && rowDiff == 1 && board[newPosition.getRow()][newPosition.getColumn()] == null) {
          return true;
      }

      boolean isStartingPosition = (color == PieceColor.WHITE && position.getRow() == 6) ||
              (color == PieceColor.BLACK && position.getRow() == 1);
      if (colDiff == 0 && rowDiff == 2 && isStartingPosition
              && board[newPosition.getRow()][newPosition.getColumn()] == null) {
          int middleRow = position.getRow() + forwardDirection;
          if (board[middleRow][position.getColumn()] == null) {
              return true;
          }
      }

      if (Math.abs(colDiff) == 1 && rowDiff == 1 && board[newPosition.getRow()][newPosition.getColumn()] != null &&
              board[newPosition.getRow()][newPosition.getColumn()].color != this.color) {
          return true;
      }

      return false;
  }
}
Rook:

public class Rook extends Piece {
  public Rook(PieceColor color, Position position) {
      super(color, position);
  }

  @Override
  public boolean isValidMove(Position newPosition, Piece[][] board) {
      if (position.getRow() == newPosition.getRow()) {
          int columnStart = Math.min(position.getColumn(), newPosition.getColumn()) + 1;
          int columnEnd = Math.max(position.getColumn(), newPosition.getColumn());
          for (int column = columnStart; column < columnEnd; column++) {
              if (board[position.getRow()][column] != null) {
                  return false;
              }
          }
      } else if (position.getColumn() == newPosition.getColumn()) {
          int rowStart = Math.min(position.getRow(), newPosition.getRow()) + 1;
          int rowEnd = Math.max(position.getRow(), newPosition.getRow());
          for (int row = rowStart; row < rowEnd; row++) {
              if (board[row][position.getColumn()] != null) {
                  return false;
              }
          }
      } else {
          return false;
      }

      Piece destinationPiece = board[newPosition.getRow()][newPosition.getColumn()];
      if (destinationPiece == null) {
          return true;
      } else if (destinationPiece.getColor() != this.getColor()) {
          return true;
      }

      return false;
  }
}
Knight:

public class Knight extends Piece {
  public Knight(PieceColor color, Position position) {
      super(color, position);
  }

  @Override
  public boolean isValidMove(Position newPosition, Piece[][] board) {
      if (newPosition.equals(this.position)) {
          return false;
      }

      int rowDiff = Math.abs(this.position.getRow() - newPosition.getRow());
      int colDiff = Math.abs(this.position.getColumn() - newPosition.getColumn());

      boolean isValidLMove = (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);

      if (!isValidLMove) {
          return false;
      }

      Piece targetPiece = board[newPosition.getRow()][newPosition.getColumn()];
      if (targetPiece == null) {
          return true;
      } else {
          return targetPiece.getColor() != this.getColor();
      }
  }
}
Bishop:

public class Bishop extends Piece {
  public Bishop(PieceColor color, Position position) {
      super(color, position);
  }

  @Override
  public boolean isValidMove(Position newPosition, Piece[][] board) {
      int rowDiff = Math.abs(position.getRow() - newPosition.getRow());
      int colDiff = Math.abs(position.getColumn() - newPosition.getColumn());

      if (rowDiff != colDiff) {
          return false;
      }

      int rowStep = newPosition.getRow() > position.getRow() ? 1 : -1;
      int colStep = newPosition.getColumn() > position.getColumn() ? 1 : -1;
      int steps = rowDiff - 1;

      for (int i = 1; i <= steps; i++) {
          if (board[position.getRow() + i * rowStep][position.getColumn() + i * colStep] != null) {
              return false;
          }
      }

      Piece destinationPiece = board[newPosition.getRow()][newPosition.getColumn()];
      if (destinationPiece == null) {
          return true;
      } else if (destinationPiece.getColor() != this.getColor()) {
          return true;
      }

      return false;
  }
}
Queen:

public class Queen extends Piece {
  public Queen(PieceColor color, Position position) {
      super(color, position);
  }

  @Override
  public boolean isValidMove(Position newPosition, Piece[][] board) {
      if (newPosition.equals(this.position)) {
          return false;
      }

      int rowDiff = Math.abs(newPosition.getRow() - this.position.getRow());
      int colDiff = Math.abs(newPosition.getColumn() - this.position.getColumn());

      boolean straightLine = this.position.getRow() == newPosition.getRow()
              || this.position.getColumn() == newPosition.getColumn();

      boolean diagonal = rowDiff == colDiff;

      if (!straightLine && !diagonal) {
          return false;
      }

      int rowDirection = Integer.compare(newPosition.getRow(), this.position.getRow());
      int colDirection = Integer.compare(newPosition.getColumn(), this.position.getColumn());

      int currentRow = this.position.getRow() + rowDirection;
      int currentCol = this.position.getColumn() + colDirection;
      while (currentRow != newPosition.getRow() || currentCol != newPosition.getColumn()) {
          if (board[currentRow][currentCol] != null) {
              return false;
          }
          currentRow += rowDirection;
          currentCol += colDirection;
      }

      Piece destinationPiece = board[newPosition.getRow()][newPosition.getColumn()];
      return destinationPiece == null || destinationPiece.getColor() != this.getColor();
  }
}
King:

public class King extends Piece {
  public King(PieceColor color, Position position) {
      super(color, position);
  }

  @Override
  public boolean isValidMove(Position newPosition, Piece[][] board) {
      int rowDiff = Math.abs(position.getRow() - newPosition.getRow());
      int colDiff = Math.abs(position.getColumn() - newPosition.getColumn());

      boolean isOneSquareMove = rowDiff <= 1 && colDiff <= 1 && !(rowDiff == 0 && colDiff == 0);

      if (!isOneSquareMove) {
          return false;
      }

      Piece destinationPiece = board[newPosition.getRow()][newPosition.getColumn()];
      return destinationPiece == null || destinationPiece.getColor() != this.getColor();
  }
}
