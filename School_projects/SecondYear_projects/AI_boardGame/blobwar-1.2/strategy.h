#ifndef __STRATEGY_H
#define __STRATEGY_H

#include "common.h"
#include "bidiarray.h"
#include "move.h"
#include <omp.h>

#include <array>
#include <cmath>
#include <string>
#include <utility> // for std::pair
#include <chrono>
#include <iostream>

#include <fstream> // Pour std::ofstream

#include <vector>
#include <algorithm>
#include <climits>


struct MinimaxResult {
    int ratio;
    movement mov;
};

struct MoveResult {
    bidiarray<Sint16> newPosition;
    int newNbRouge;
    int newNbBleu;
};



class Strategy {

private:
    //! array containing all blobs on the board
    bidiarray<Sint16> _blobs;
    //! an array of booleans indicating for each cell whether it is a hole or not.
    const bidiarray<bool>& _holes;
    //! Current player
    Uint16 _current_player;
    
    //! Call this function to save your best move.
    //! Multiple call can be done each turn,
    //! Only the last move saved will be used.
    void (*_saveBestMove)(movement&);

public:
        // Constructor from a current situation
    Strategy (bidiarray<Sint16>& blobs, 
              const bidiarray<bool>& holes,
              const Uint16 current_player,
              void (*saveBestMove)(movement&))
            : _blobs(blobs),_holes(holes), _current_player(current_player), _saveBestMove(saveBestMove)
        {
        }
    
              
    
        // Copy constructor
    Strategy (const Strategy& St)
            : _blobs(St._blobs), _holes(St._holes),_current_player(St._current_player) 
        {}
    
        // Destructor
    ~Strategy() {}

    void prettyPrintPlateau(const bidiarray<Sint16>& array);

    movement algoGlouton(int nbRouge, int nbBleu, int myColor);

    MinimaxResult minimax(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool currentJoueur);
    int minimaxClear(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax);
    // int minimaxPara(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax);
    // int minimaxParaWorker(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax);

    movement find_best_move_min_max(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax);

    MinimaxResult alphaBeta(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool currentJoueur, int alpha, int beta);

    MinimaxResult alphaBetaOpti(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax, int alpha, int beta);

    MinimaxResult alphaBetaOpti_V2(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax, int alpha, int beta);

    MinimaxResult alphaBetaParallel(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax, int alpha, int beta);


        // int eval_position(int nbRouge, int nbBleu);

    int calcDistance (Uint8 aX, Uint8 aY, Uint8 bX, Uint8 bY);

    int getAdversaireAutour (Uint8 blobX, Uint8 blobY, int myColor);

    int calcRatioMouv(movement mv, int nbRouge, int nbBleu, int myColor);

    std::vector<MoveResult> generate_moves(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, bool joueurMax);



        /** 
         * Apply a move to the current state of blobs
         * Assumes that the move is valid
         */
    bidiarray<Sint16> applyMove(int& arg1, int& arg2, const movement& mv, const bidiarray<Sint16>& array, bool flag);

        /**
         * Compute the vector containing every possible moves
         */
    vector<movement>& computeValidMoves (vector<movement>& valid_moves, int player, const bidiarray<Sint16>& array) const;

    vector<pair<int, movement>>& computeValidMovesOpti_V1(vector<pair<int, movement>>& ratios_and_moves, int player, const bidiarray<Sint16>& array, int nbRouge, int nbBleu);

    vector<pair<int, movement>>& computeValidMovesOpti_V2(vector<pair<int, movement>>& ratios_and_moves, int player, const bidiarray<Sint16>& array, int nbRouge, int nbBleu);


        /**
         * Estimate the score of the current state of the game
         */
    std::array<int, 2> estimateCurrentScore() const;

    std::array<int, 3> estimateCurrentScore_NombreTrous();

        /**
         * Find the best move.
         */
    void computeBestMove ();


    
};

#endif
