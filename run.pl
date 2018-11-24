:- [facts].

%Actions%
action(attack, 0, 0).
action(take, 0, 0).
action(up, -1, 0).
action(right, 0, 1).
action(down, 1, 0).
action(left, 0, -1).

% Goal_test checks that in State S
% all white walkers are dead
goal_test(S):-
    foreach(whiteWalker(X, Y), (empty(X, Y, S))), noGoalStateBefore(S).

% Checks that there doesnt 
% occur any goal before State S
noGoalStateBefore(s0).
noGoalStateBefore(result(_, S)):-
    \+ goal_test(S),
    noGoalStateBefore(S).

%Base case for empty predicate
empty(X, Y, s0):-
    emptyInit(X, Y).

% A cell is empty if a white walker was on it
% and Jon was able from his position to attack this cell
% and had enough dragon glass OR the 
% cell was already empty from the previous state
empty(X, Y, result(A, S)):-
    action(A, _, _), noGoalStateBefore(result(A, S)), jon(W, Z, S), currentDragon(V, S),   
    (
        ( 
            A = attack, whiteWalker(X, Y), 
            checkValidAttack(X, Y, W, Z),
            \+empty(X, Y, S),
            V \= 0
        );
        (
            empty(X, Y, S)
        )
    ).


%Base case for jon predicate
jon(X, Y, s0):-
    jonInit(X, Y).

% Jon is located in the cell X, Y
% if and only if it was a valid Move
jon(X, Y, result(A, S)):-
    (
        action(A, DX, DY),
        jon(OLDX, OLDY, S),
        checkValidMove(OLDX, OLDY, DX, DY),
        X is OLDX + DX, Y is OLDY + DY,
        empty(X, Y, S)
    ).

%Base case for currentDragon predicate    
currentDragon(X, s0):-
    currentDragonInit(X).  

% Jon has D number of dragon glass if and only if
% Jon was able to attack a white walker and he didnt have
% dragon glass in the previous state the D = previous dragon glass - 1
% OR he was able to take dragon glass from the dragon Stone then D = Max Dragon OR
% he was able to move in a cell then D = previous dragon glass number
currentDragon(MD, result(A, S)):-
    action(A, _, _), currentDragon(V, S),
    jon(W, Z, S),
    (
        %Taking dragonGlass%
        (
            A = take, dragonStone(W, Z),
            maxDragon(MD), V < MD
        );
         %Attacking%
        (
            A = attack, whiteWalker(X, Y), checkValidAttack(X, Y, W, Z),
            \+ empty(X, Y, S),
            V \= 0,
            MD is V-1
        );
        %Moving%
        (
            A \= attack, A \= take,
            MD = V
        )
    ).


%Checks that Jon can move to a certain cell
checkValidMove(X, Y, DX, DY):-
    gridSize(I, J),
    P is X + DX,
    P2 is Y + DY,
    P >= 0, P < I,
    P2 >= 0, P2 < J.

%Checks jon is able to attack a white Walker
checkValidAttack(X, Y, W, Z):-
    gridSize(I, J),
    P is W+1,
    P2 is W-1,
    P3 is Z-1,
    P4 is Z+1,
    (
        (X = P, Y = Z, X>=0, X<I);
        (X = P2, Y = Z, X>=0, X<I);
        (X = W, Y = P3, P3>=0, P3<J);
        (X = W, Y = P4, P4>=0, P4<J)
    ).

%Main function to get Results (Query)
plan():-
    call_with_depth_limit(goal_test(S), 16, Depth),
    Depth \= depth_limit_exceeded,
    actionFormat(S, A),
    reverse(A, R),
    print(R).
    

% manipulating the result from the goal state
% to a more convenient representation
actionFormat(S, []):-
    S \= result(_, _).

actionFormat(S, [H|T]):-
    S = result(H, S2),
    actionFormat(S2, T).    


