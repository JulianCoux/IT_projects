#include "strategy.h"

int nbNoeudsArbre = 0;


bidiarray<Sint16> Strategy::applyMove(int& nbRouge, int& nbBleu, const movement& mv, const bidiarray<Sint16>& array, bool joueurMax) {
    // Fait une copie du plateau
    bidiarray<Sint16> newPosition = array;
    // printf("De (%d, %d) à (%d, %d)\n", mv.ox, mv.oy, mv.nx, mv.ny);
    if (mv.ox - mv.nx == 2 || mv.ox - mv.nx == -2 || mv.oy - mv.ny == 2 || mv.oy - mv.ny == -2){  //savoir si on est a une distance de 2
        newPosition.set(mv.ox, mv.oy, -1);
        if (joueurMax){
            nbRouge --;
        } else {
            nbBleu --;
        }
    }

    if(joueurMax){
        newPosition.set(mv.nx, mv.ny, 0);
        nbRouge ++;
    }else{
        newPosition.set(mv.nx, mv.ny, 1);
        nbBleu ++;
    }

    for (int k = 0; k < 3; ++k){
        for (int l = 0; l < 3; ++l){
            int x = mv.nx + (k - 1);
            int y = mv.ny + (l - 1);
            if (0 <= x && x < 8 && 0 <= y && y < 8){
                if (newPosition.get(x, y) == 1 && joueurMax){
                    // On modif le bleu en rouge
                    newPosition.set(x, y, 0);
                    nbBleu --;
                    nbRouge ++;
                }
                else if (newPosition.get(x, y) == 0 && !joueurMax){
                    // On modif le rouge en bleu
                    newPosition.set(x, y, 1);
                    nbBleu ++;
                    nbRouge --;
                }
            }
        }
    }

    return newPosition;
}



void Strategy::prettyPrintPlateau (const bidiarray<Sint16>& array) {
    //joli print du plateau :
    movement mv(0,0,0,0);

    printf("+ - - - - - - - - +\n");
    for(mv.oy = 0 ; mv.oy < 8 ; mv.oy++) {
        std::string chaine = "| ";
        for(mv.ox = 0 ; mv.ox < 8 ; mv.ox++) {
            if(array.get(mv.ox, mv.oy) == 1){  // c'est un Bleu
                chaine.append("\033[34mB\033[0m "); // Afficher "B" en bleu
            }
            else if(array.get(mv.ox, mv.oy) == 0){  // c'est un Rouge
                chaine.append("\033[31mR\033[0m "); // Afficher "R" en rouge
            }
            else {
                if(_holes.get(mv.ox, mv.oy) == true){  // c'est un trou
                    chaine.append("\033[37mx\033[0m ");
                }
                else{  //c'est une case vide
                    chaine.append(". ");
                }
            }
        }
        chaine.append("|");
        std::cout << chaine << std::endl;
    }
    printf("+ - - - - - - - - +\n");
}



int Strategy::getAdversaireAutour (Uint8 blobX, Uint8 blobY, int myColor){
    movement mv(blobX, blobY, 0, 0);
    uint8_t adversaireColor = 0;
    int nbColorAdversaire = 0;
    if(myColor == 0){
        adversaireColor = 1;
    }

    for(mv.nx = std::max(0,mv.ox-1) ; mv.nx <= std::min(7,mv.ox+1) ; mv.nx++) {
        for(mv.ny = std::max(0,mv.oy-1) ; mv.ny <= std::min(7,mv.oy+1) ; mv.ny++) {
            if(_blobs.get(mv.nx, mv.ny) == adversaireColor){
                nbColorAdversaire ++;
            }
        }
    }

    return nbColorAdversaire;
}


int Strategy::calcDistance (Uint8 aX, Uint8 aY, Uint8 bX, Uint8 bY){
    //Utilisé seulement dans algoGlouton
    int distanceX = bX - aX;
    int distanceY = bY - aY;
    return sqrt(distanceX * distanceX + distanceY * distanceY);
}


std::array<int, 2> Strategy::estimateCurrentScore() const {
    movement mv(0,0,0,0);

    int nbBlobRed = 0;
    int nbBlobBlue = 0;
    for(mv.ox = 0 ; mv.ox < 8 ; mv.ox++) {
        for(mv.oy = 0 ; mv.oy < 8 ; mv.oy++) {
            //0 => rouge
            //1 => bleu
            //-1 => rien (soit pas de pion, soit un trou)
            if(_blobs.get(mv.ox, mv.oy) == 0){
                nbBlobRed++;
            }
            else if(_blobs.get(mv.ox, mv.oy) == 1){
                nbBlobBlue++;
            }
        }
    }

    // Créer un std::array et y stocker les nombres de pions rouges et bleus
    std::array<int, 2> scores = {nbBlobRed, nbBlobBlue};

    // Renvoyer le std::array contenant les nombres de pions rouges et bleus
    return scores;
}

std::array<int, 3> Strategy::estimateCurrentScore_NombreTrous(){
    movement mv(0,0,0,0);

    int nbBlobRed = 0;
    int nbBlobBlue = 0;
    int nbTrous = 0;
    for(mv.ox = 0 ; mv.ox < 8 ; mv.ox++) {
        for(mv.oy = 0 ; mv.oy < 8 ; mv.oy++) {
            //0 => rouge
            //1 => bleu
            //-1 => rien (soit pas de pion, soit un trou)
            if(_blobs.get(mv.ox, mv.oy) == 0){
                nbBlobRed++;
            }
            else if(_blobs.get(mv.ox, mv.oy) == 1){
                nbBlobBlue++;
            }
            else if(_holes.get(mv.ox, mv.oy) == true){
                nbTrous ++;
            }
        }
    }

    // Créer un std::array et y stocker les nombres de pions rouges et bleus
    std::array<int, 3> scores = {nbBlobRed, nbBlobBlue, nbTrous};

    // Renvoyer le std::array contenant les nombres de pions rouges et bleus
    return scores;
}


vector<movement>& Strategy::computeValidMoves (vector<movement>& valid_moves, int player, const bidiarray<Sint16>& array) const {
    valid_moves.clear();
    movement mv(0, 0, 0, 0);

    for (mv.ox = 0; mv.ox < 8; mv.ox++) {
        for (mv.oy = 0; mv.oy < 8; mv.oy++) {
            if (array.get(mv.ox, mv.oy) == player) {
                // Iterate on possible destinations
                for (mv.nx = std::max(0, mv.ox - 2); mv.nx <= std::min(7, mv.ox + 2); mv.nx++) {
                    for (mv.ny = std::max(0, mv.oy - 2); mv.ny <= std::min(7, mv.oy + 2); mv.ny++) {
                        if (!_holes.get(mv.nx, mv.ny) && array.get(mv.nx, mv.ny) == -1) {
                            // mouvement valid
                            valid_moves.push_back(mv);
                        }
                    }
                }
            }
        }
    }

    return valid_moves;
}

/*
bool comparePairs(const std::pair<int, movement>& a, const std::pair<int, movement>& b) {
    return a.first > b.first; // Trie en ordre décroissant
}

// Fonction pour trier le tableau de paires en fonction des ratios
void sortRatiosAndMoves(std::vector<std::pair<int, movement>>& ratios_and_moves) {
    // Tri par sélection
    for (size_t i = 0; i < ratios_and_moves.size() - 1; ++i) {
        size_t max_idx = i;
        for (size_t j = i + 1; j < ratios_and_moves.size(); ++j) {
            if (comparePairs(ratios_and_moves[j], ratios_and_moves[max_idx])) {
                max_idx = j;
            }
        }
        if (max_idx != i) {
            std::swap(ratios_and_moves[i], ratios_and_moves[max_idx]);
        }
    }
}


// Partitionne le tableau et retourne l'index du pivot
int partition(std::vector<std::pair<int, movement>>& tab, int low, int high) {
    int pivot = tab[low].first; // Choix du pivot
    int i = low - 1; // Index du plus petit élément
    int j = high + 1;

    while (true) {
        do {
            j--;
        } while (tab[j].first > pivot);

        do {
            i++;
        } while (tab[i].first < pivot);

        if (i < j) {
            std::swap(tab[i].first, tab[j].first);
        }
        else {
            return j;
        }
    }
}

// Tri rapide récursif
void quickSort(std::vector<std::pair<int, movement>>& tab, int low, int high) {
    if (low < high) {
        // Partitionne le tableau
        int pi = partition(tab, low, high);

        // Trie les deux sous-tableaux de manière récursive
        quickSort(tab, low, pi);
        quickSort(tab, pi + 1, high);
    }
}

// Fonction d'interface pour trier le tableau
void sortRatiosAndMoves(std::vector<std::pair<int, movement>>& ratios_and_moves) {
    int n = ratios_and_moves.size();
    quickSort(ratios_and_moves, 0, n - 1);
}*/

vector<pair<int, movement>>& Strategy::computeValidMovesOpti_V1 (vector<pair<int, movement>>& ratios_and_moves, int player, const bidiarray<Sint16>& array, int nbRouge, int nbBleu){
    ratios_and_moves.clear();
    movement mv(0, 0, 0, 0);
//    int ratioMoyen = 0;
    int maxRatio = -10000;

    for (mv.ox = 0; mv.ox < 8; mv.ox++) {
        for (mv.oy = 0; mv.oy < 8; mv.oy++) {
            if (array.get(mv.ox, mv.oy) == player) {
                // Iterate on possible destinations
                for (mv.nx = std::max(0, mv.ox - 2); mv.nx <= std::min(7, mv.ox + 2); mv.nx++) {
                    for (mv.ny = std::max(0, mv.oy - 2); mv.ny <= std::min(7, mv.oy + 2); mv.ny++) {
                        if (!_holes.get(mv.nx, mv.ny) && array.get(mv.nx, mv.ny) == -1) {
                            // mouvement valid
                            int ratio = calcRatioMouv(mv, nbRouge, nbBleu, player);

                            if(ratio > maxRatio){
                                maxRatio = ratio;
                                ratios_and_moves.insert(ratios_and_moves.begin(), make_pair(ratio, mv));
                                continue;
                            }
                            else{
                                ratios_and_moves.push_back(make_pair(ratio, mv));
                            }
                        }
                    }
                }
            }
        }
    }

    /*if(!ratios_and_moves.empty()){
        sortRatiosAndMoves(ratios_and_moves);
    }*/
    //ratio_and_moves est maintenant trié
    return ratios_and_moves;
}

vector<pair<int, movement>>& Strategy::computeValidMovesOpti_V2(vector<pair<int, movement>>& ratios_and_moves, int player, const bidiarray<Sint16>& array, int nbRouge, int nbBleu){
    ratios_and_moves.clear();
    movement mv(0, 0, 0, 0);
    int ratioMoyen = 0;
    int nbRatioCalc = 0;
    int maxRatio = -10000;
    int minRatio = 10000;

    for (mv.ox = 0; mv.ox < 8; mv.ox++) {
        for (mv.oy = 0; mv.oy < 8; mv.oy++) {
            if (array.get(mv.ox, mv.oy) == player) {
                // Iterate on possible destinations
                for (mv.nx = std::max(0, mv.ox - 2); mv.nx <= std::min(7, mv.ox + 2); mv.nx++) {
                    for (mv.ny = std::max(0, mv.oy - 2); mv.ny <= std::min(7, mv.oy + 2); mv.ny++) {
                        if (!_holes.get(mv.nx, mv.ny) && array.get(mv.nx, mv.ny) == -1) {
                            // mouvement valid
                            int ratio = calcRatioMouv(mv, nbRouge, nbBleu, player);

                            ratioMoyen += ratio;
                            nbRatioCalc ++;

                            if(ratio > maxRatio){
                                maxRatio = ratio;
                                ratios_and_moves.insert(ratios_and_moves.begin(), make_pair(ratio, mv));
                                continue;
                            }
                            if(ratio <= minRatio){
                                minRatio = ratio;
                                ratios_and_moves.push_back(make_pair(ratio, mv));
                                continue;
                            }

                            //Technique pour trier a peu près
                            int moyenne = ratioMoyen / nbRatioCalc;
                            int distMax = abs(ratio - maxRatio);
                            int distMoy = abs(ratio - moyenne);
                            int distMin = abs(ratio - minRatio);
                            //on pourrait encore découper en plusieurs sections pour être précis
                            if(distMoy < distMax && distMoy < distMin){
                                int milieu = ratios_and_moves.size() / 2;
                                ratios_and_moves.insert(ratios_and_moves.begin() + milieu, make_pair(ratio, mv));
                            }
                            else if(distMin < distMax){
                                ratios_and_moves.push_back(make_pair(ratio, mv)); // Sauvegarde du ratio et du mouvement dans le tableau
                            }
                            else{
                                ratios_and_moves.insert(ratios_and_moves.begin() + 1, make_pair(ratio, mv));
                            }

                            //printf("-- -- -- -- --\nRatio calculé : %d\n-- -- -- -- --\n", ratio);
                        }
                    }
                }
            }
        }
    }

    /*if(!ratios_and_moves.empty()){
        sortRatiosAndMoves(ratios_and_moves);
    }*/
    //ratio_and_moves est maintenant trié
    return ratios_and_moves;
}

int Strategy::calcRatioMouv(movement mv, int nbRouge, int nbBleu, int myColor){
    int nbAdversaireAutour = getAdversaireAutour(mv.nx, mv.ny, myColor);
    int nombreBleu = nbBleu;
    int nombreRouge = nbRouge;

    if(myColor == 0) {  //joueur Rouge
        if (calcDistance(mv.ox, mv.oy, mv.nx, mv.ny) == 1) {
            nombreRouge ++;
        }
        nombreRouge += nbAdversaireAutour;
        nombreBleu -= nbAdversaireAutour;
        return nombreRouge - nombreBleu;
    }
    else{
        if (calcDistance(mv.ox, mv.oy, mv.nx, mv.ny) == 1) {
            nombreBleu ++;
        }
        nombreBleu += nbAdversaireAutour;
        nombreRouge -= nbAdversaireAutour;
        return nombreBleu - nombreRouge;
    }
}


void Strategy::computeBestMove () {
    if((int) _current_player == 1){ //je suis Bleu
        std::string chaine = "[";
        chaine.append("\033[34mEval de BLEU\033[0m");
        chaine.append("] :");
        std::cout << chaine << std::endl;
    }
    else{
        std::string chaine = "[";
        chaine.append("\033[31mEval de ROUGE\033[0m");
        chaine.append("] :");
        std::cout << chaine << std::endl;
    }

    std::array<int, 2> score = estimateCurrentScore();
    //std::array<int, 3> score = estimateCurrentScore_NombreTrous();
    int nbBlobRed = score[0];
    int nbBlobBlue = score[1];
//    int nbTrous = score[2];

    //printf("Nombre Trous = %d\n", nbTrous);

    std::ofstream outputRedFile("debugFiles/rouge.txt", std::ios::app);
    std::ofstream outputBlueFile("debugFiles/bleu.txt", std::ios::app);

    // Vérifier si l'ouverture du fichier a réussi
    if (!outputRedFile.is_open()) {
        std::cerr << "Erreur lors de l'ouverture du fichier !" << std::endl;
    }
    if (!outputBlueFile.is_open()) {
        std::cerr << "Erreur lors de l'ouverture du fichier !" << std::endl;
    }

    //getchar();

    MinimaxResult result;
    movement mv;

    auto start_time = std::chrono::high_resolution_clock::now(); // Temps avant minimax

    if ((int) _current_player == 0){    // On est Rouge
         int alpha = -10000;
         int beta = 10000;
         result = alphaBetaOpti_V2(nbBlobRed, nbBlobBlue, _blobs, 1, true, alpha, beta);
//         result = minimax(nbBlobRed, nbBlobBlue, _blobs, 3, true);
         mv = result.mov;
        //mv = find_best_move_min_max(nbBlobRed, nbBlobBlue, _blobs, 3, true);
    }
    else {      // On est Bleu
        //mv = algoGlouton(nbBlobRed, nbBlobBlue, (int) _current_player);
        //*result = minimax(nbBlobRed, nbBlobBlue, _blobs, 4, false);
        int alpha = -10000;
        int beta = 10000;
        result = alphaBetaOpti_V2(nbBlobRed, nbBlobBlue, _blobs, 5, false, alpha, beta);
        mv = result.mov;
         //mv = find_best_move_min_max(nbBlobRed, nbBlobBlue, _blobs, 3, false);
    }

    auto end_time = std::chrono::high_resolution_clock::now(); // Temps après minimax
    // Calcul de la durée écoulée
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end_time - start_time);

    goto Debug;

    //goto end_choice;

Debug:
    prettyPrintPlateau(_blobs);
    std::cout << "Temps calcul ia : " << duration.count() << " millisecondes" << std::endl;
    printf("\n[NOMBRE NOEUDS ARBRE] : %d\n", nbNoeudsArbre);
    printf("\n[Meilleur mouvement trouvé] : \n");
    printf("     [Position initiale] : ox = %d, oy = %d\n", mv.ox, mv.oy);
    printf("     [Position finale] : nx = %d, ny = %d\n", mv.nx, mv.ny);

    if((int) _current_player == 0){
        outputRedFile << duration.count() << " " << nbNoeudsArbre << std::endl;
    }
    else{
        outputBlueFile << duration.count() << " " << nbNoeudsArbre << std::endl;
    }
    outputRedFile.close();
    outputBlueFile.close();

    goto end_choice;

end_choice:
     _saveBestMove(mv);
     return;
}

movement Strategy::algoGlouton(int nbRouge, int nbBleu, int myColor){
    //maximum sur tous les pions
    int maxRatioAllPions = -1000;
    movement bestMovAllPions(0,0,0,0);

    //maximum pour un pion
    int maxRatioPion = -1000;   //=> contient le meilleur ratio de ce pion
    movement bestMovPion(0, 0, 0, 0);  //=> contient le meilleur déplacement à effectuer

    movement mv(0,0,0,0);
    for(mv.ox = 0 ; mv.ox < 8 ; mv.ox++) {
        for(mv.oy = 0 ; mv.oy < 8 ; mv.oy++) {
            if (_blobs.get(mv.ox, mv.oy) == (int) _current_player) {
                maxRatioPion = -1000;
                bestMovPion.ox = mv.ox;
                bestMovPion.oy = mv.oy;

                printf("\n[Pion ciblé] : ox = %d, oy = %d\n", mv.ox, mv.oy);

                for(mv.nx = std::max(0,mv.ox-2) ; mv.nx <= std::min(7,mv.ox+2) ; mv.nx++) {
                    for(mv.ny = std::max(0,mv.oy-2) ; mv.ny <= std::min(7,mv.oy+2) ; mv.ny++) {
                        if (_holes.get(mv.nx, mv.ny)) continue;     //si c'est un trou, on ne peut rien faire
                        if(_blobs.get(mv.nx, mv.ny) == -1){               //si la case est libre
                            printf("   [déplacement valide] : nx = %d, ny = %d\n", mv.nx, mv.ny);
                            int nbAdversaireAutour = getAdversaireAutour(mv.nx, mv.ny, myColor);
                            printf("        [nombre adversaires autour] : %d\n", nbAdversaireAutour);
                            //si la distance entre mv.ox, mv.oy et mv.nx, mv.ny est = à 1:
                            int rougeEnPlus = 0;
                            if (calcDistance(mv.ox, mv.oy, mv.nx, mv.ny) == 1) {
                                rougeEnPlus = 1;
                            }

                            int currentRatio = 0;
                            if(myColor==1){     //je suis bleu
                                currentRatio = (nbBleu + rougeEnPlus + nbAdversaireAutour)-(nbRouge - nbAdversaireAutour);
                            }
                            else{
                                currentRatio = (nbRouge + rougeEnPlus + nbAdversaireAutour)-(nbBleu - nbAdversaireAutour);
                            }
                            printf("        [ratio calculé] : %d\n", currentRatio);
                            if(currentRatio > maxRatioPion){
                                printf("             [bestMov] : C'est un meilleur ratio pour ce pion\n");
                                maxRatioPion = currentRatio;
                                bestMovPion.nx = mv.nx;
                                bestMovPion.ny = mv.ny;
                            }
                        }
                        //fin de la boucle
                    }
                }
                //fin des for
                //on veut ajouter maxRatioPion et bestMovPion dans tabMaxRatioPion
                if(maxRatioPion > maxRatioAllPions){
                    maxRatioAllPions = maxRatioPion;
                    bestMovAllPions = bestMovPion;
                }
            }
        }
    }

    return bestMovAllPions;
}




MinimaxResult Strategy::minimax(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax) {
    // printf("\n[On rentre dans le minimax] : nbBleu = %i, nbRouge = %i, profondeur = %i, Au tour de %d \n", nbBleu, nbRouge, profondeur, joueurMax);
    nbNoeudsArbre ++;
    if (profondeur == 0){
        MinimaxResult result{nbRouge - nbBleu, movement(0, 0, 0, 0)}; // On pourrait ne pas créer d'objet et renvoyer null mais en c++ j'arrive pas
        return result;
    }

    int currentJoueur = joueurMax ? 0 : 1;
    int bestRatio = joueurMax ? -10000 : 10000;
    movement bestMov(0, 0, 0, 0);
    vector<movement> valid_moves;
    computeValidMoves(valid_moves, currentJoueur, plateau);

    if(valid_moves.empty()){    //si il n'y a aucun mouvement possible
        MinimaxResult result{nbRouge - nbBleu, movement(0, 0, 0, 0)};
        return result;
    }

    for (size_t i = 0; i < valid_moves.size(); ++i) {
        const movement& move = valid_moves[i];

        // appliquer le coup
        int newNbRouge = nbRouge;
        int newNbBleu = nbBleu;
        bidiarray<Sint16> nouvellePos = applyMove(newNbRouge, newNbBleu, move, plateau, joueurMax);

        MinimaxResult res = minimax(newNbRouge, newNbBleu, nouvellePos, profondeur-1, !joueurMax);

        if(joueurMax){ //joueur Rouge
            if (res.ratio > bestRatio){
                bestRatio = res.ratio;
                bestMov = move;
            }
        }
        else{  //joueur Bleu
            if (res.ratio < bestRatio){
                bestRatio = res.ratio;
                bestMov = move;
            }
        }
    }

    MinimaxResult result{bestRatio, bestMov};
    return result;
}




MinimaxResult Strategy::alphaBeta(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax, int alpha, int beta) {
    nbNoeudsArbre ++;

    if (profondeur == 0){
        MinimaxResult result{nbRouge - nbBleu, movement(0, 0, 0, 0)};
        return result;
    }

    int currentJoueur = joueurMax ? 0 : 1;
    int bestRatio = joueurMax ? -10000 : 10000;
    movement bestMov(0, 0, 0, 0);
    vector<movement> valid_moves;
    computeValidMoves(valid_moves, currentJoueur, plateau);

    if(valid_moves.empty()){    //si il n'y a aucun mouvement possible
        MinimaxResult result{nbRouge - nbBleu, movement(0, 0, 0, 0)};
        return result;
    }

    for (size_t i = 0; i < valid_moves.size(); ++i) {
        const movement& move = valid_moves[i];

        // appliquer le coup
        int newNbRouge = nbRouge;
        int newNbBleu = nbBleu;
        bidiarray<Sint16> nouvellePos = applyMove(newNbRouge, newNbBleu, move, plateau, joueurMax);

        MinimaxResult eval = alphaBeta(newNbRouge, newNbBleu, nouvellePos, profondeur-1, !joueurMax, alpha, beta);

        if(joueurMax){
            if (eval.ratio > bestRatio){
                bestRatio = eval.ratio;
                bestMov = move;
            }
            alpha = max(alpha, eval.ratio);
            if(beta <= alpha){
                break;
            }
        }
        else{
            if (eval.ratio < bestRatio){
                bestRatio = eval.ratio;
                bestMov = move;
            }
            beta = min(beta, eval.ratio);
            if(beta <= alpha){
                break;
            }
        }
    }

    MinimaxResult result{bestRatio, bestMov};
    return result;
}


/*
 * Algo alhpa beta qui ordonne les les coups en fonction de leur meilleur ratio afin d'élaguer un maximum de branches
 */
MinimaxResult Strategy::alphaBetaOpti(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax, int alpha, int beta) {
    nbNoeudsArbre ++;

    if (profondeur == 0){
        MinimaxResult result{nbRouge - nbBleu, movement(0, 0, 0, 0)};
        return result;
    }

    int currentJoueur = joueurMax ? 0 : 1;
    int bestRatio = joueurMax ? -10000 : 10000;
    movement bestMov(0, 0, 0, 0);
    vector<pair<int, movement>> valid_moves;
    computeValidMovesOpti_V1(valid_moves, currentJoueur, plateau, nbRouge, nbBleu);

    if(valid_moves.empty()){    //si il n'y a aucun mouvement possible
        MinimaxResult result{nbRouge - nbBleu, movement(0, 0, 0, 0)};
        return result;
    }

    for (size_t i = 0; i < valid_moves.size(); ++i) {
        const movement& move = valid_moves[i].second;

        // appliquer le coup
        int newNbRouge = nbRouge;
        int newNbBleu = nbBleu;
        bidiarray<Sint16> nouvellePos = applyMove(newNbRouge, newNbBleu, move, plateau, joueurMax);

        MinimaxResult eval = alphaBetaOpti(newNbRouge, newNbBleu, nouvellePos, profondeur-1, !joueurMax, alpha, beta);

        if(joueurMax){
            if (eval.ratio > bestRatio){
                bestRatio = eval.ratio;
                bestMov = move;
            }
            alpha = max(alpha, eval.ratio);
            if(beta <= alpha){
                break;
            }
        }
        else{
            if (eval.ratio < bestRatio){
                bestRatio = eval.ratio;
                bestMov = move;
            }
            beta = min(beta, eval.ratio);
            if(beta <= alpha){
                break;
            }
        }
    }

    MinimaxResult result{bestRatio, bestMov};
    return result;
}

MinimaxResult Strategy::alphaBetaOpti_V2(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax, int alpha, int beta) {
    nbNoeudsArbre ++;

    if (profondeur == 0){
        MinimaxResult result{nbRouge - nbBleu, movement(0, 0, 0, 0)};
        return result;
    }

    int currentJoueur = joueurMax ? 0 : 1;
    int bestRatio = joueurMax ? -10000 : 10000;
    movement bestMov(0, 0, 0, 0);
    vector<pair<int, movement>> valid_moves;
    computeValidMovesOpti_V2(valid_moves, currentJoueur, plateau, nbRouge, nbBleu);

    if(valid_moves.empty()){    //si il n'y a aucun mouvement possible
        MinimaxResult result{nbRouge - nbBleu, movement(0, 0, 0, 0)};
        return result;
    }

    for (size_t i = 0; i < valid_moves.size(); ++i) {
        const movement& move = valid_moves[i].second;

        // appliquer le coup
        int newNbRouge = nbRouge;
        int newNbBleu = nbBleu;
        bidiarray<Sint16> nouvellePos = applyMove(newNbRouge, newNbBleu, move, plateau, joueurMax);

        MinimaxResult eval = alphaBetaOpti(newNbRouge, newNbBleu, nouvellePos, profondeur-1, !joueurMax, alpha, beta);

        if(joueurMax){
            if (eval.ratio > bestRatio){
                bestRatio = eval.ratio;
                bestMov = move;
            }
            alpha = max(alpha, eval.ratio);
            if(beta <= alpha){
                break;
            }
        }
        else{
            if (eval.ratio < bestRatio){
                bestRatio = eval.ratio;
                bestMov = move;
            }
            beta = min(beta, eval.ratio);
            if(beta <= alpha){
                break;
            }
        }
    }

    MinimaxResult result{bestRatio, bestMov};
    return result;
}


MinimaxResult Strategy::alphaBetaParallel(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax, int alpha, int beta) {
    nbNoeudsArbre ++;

    if (profondeur == 0){
        MinimaxResult result{nbRouge - nbBleu, movement(0, 0, 0, 0)};
        return result;
    }

    int currentJoueur = joueurMax ? 0 : 1;
    int bestRatio = joueurMax ? -10000 : 10000;
    movement bestMov(0, 0, 0, 0);
    bool stopSearch = false;
    vector<movement> valid_moves;
    computeValidMoves(valid_moves, currentJoueur, plateau);

    if(valid_moves.empty()){    //si il n'y a aucun mouvement possible
        MinimaxResult result{nbRouge - nbBleu, movement(0, 0, 0, 0)};
        return result;
    }

#pragma omp parallel shared(bestRatio, bestMov, stopSearch)
    {
#pragma omp for reduction(min:beta) reduction(max:alpha)
        for (size_t i = 0; i < valid_moves.size(); ++i) {
            const movement& move = valid_moves[i];

            // appliquer le coup
            int newNbRouge = nbRouge;
            int newNbBleu = nbBleu;
            bidiarray<Sint16> nouvellePos = applyMove(newNbRouge, newNbBleu, move, plateau, joueurMax);

            MinimaxResult eval = alphaBeta(newNbRouge, newNbBleu, nouvellePos, profondeur-1, !joueurMax, alpha, beta);

#pragma omp critical
            {
                if(joueurMax){
                    if (eval.ratio > bestRatio){
                        bestRatio = eval.ratio;
                        bestMov = move;
                    }
                    alpha = max(alpha, eval.ratio);
                    if(beta <= alpha){
                        stopSearch = true;
                    }
                }
                else{
                    if (eval.ratio < bestRatio){
                        bestRatio = eval.ratio;
                        bestMov = move;
                    }
                    beta = min(beta, eval.ratio);
                    if(beta <= alpha){
                        stopSearch = true;
                    }
                }
            }
            if (stopSearch) {
#pragma omp cancel for
            }
        }
    }



    MinimaxResult result{bestRatio, bestMov};
    return result;
}




int eval_position(int nbBlobRed, int nbBlobBlue) {
    return nbBlobRed - nbBlobBlue;
}


int Strategy::minimaxClear(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax) {
    nbNoeudsArbre ++;
    if (profondeur == 0){
        return eval_position(nbRouge, nbBleu);
    }
    int bestRatio = joueurMax ? -10000 : 10000;

    vector<MoveResult> new_positions = generate_moves(nbRouge, nbBleu, plateau, joueurMax);

    if(new_positions.empty()){    //si il n'y a aucun mouvement possible
        return eval_position(nbRouge, nbBleu);
    }

    // Parcourir le vecteur new_positions avec OpenMP
    #pragma omp parallel for shared(bestRatio)
    for (size_t i = 0; i < new_positions.size(); ++i) {
        const auto& result = new_positions[i];
        const auto& newPosition = result.newPosition;
        int newNbRouge = result.newNbRouge;
        int newNbBleu = result.newNbBleu;

        int eval_child = minimaxClear(newNbRouge, newNbBleu, newPosition, profondeur - 1, !joueurMax);

        // Utiliser une section critique pour mettre à jour bestRatio
    #pragma omp critical
        {
            if (joueurMax) { // Joueur Rouge
                if (eval_child > bestRatio) {
                    bestRatio = eval_child;
                }
            } else { // Joueur Bleu
                if (eval_child < bestRatio) {
                    bestRatio = eval_child;
                }
            }
        }
    }

    return bestRatio;
}

movement Strategy::find_best_move_min_max(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax){
    movement bestMov(0, 0, 0, 0);
    int currentJoueur = joueurMax ? 0 : 1;
    int bestRatio = joueurMax ? -10000 : 10000;
    vector<movement> valid_moves;
    computeValidMoves(valid_moves, currentJoueur, plateau);

    // Parcourir valid_moves avec OpenMP
    #pragma omp parallel for shared(bestRatio, bestMov)
    for (size_t i = 0; i < valid_moves.size(); ++i) {
        const movement& move = valid_moves[i];
        // appliquer le coup
        int newNbRouge = nbRouge;
        int newNbBleu = nbBleu;
        bidiarray<Sint16> nouvellePos = applyMove(newNbRouge, newNbBleu, move, plateau, joueurMax);

        int eval_move = minimaxClear(newNbRouge, newNbBleu, nouvellePos, profondeur, !joueurMax);
        printf("Eval Move : %d\n", eval_move);

        // Utiliser une section critique pour mettre à jour bestRatio et bestMov
    #pragma omp critical
        {
            if (joueurMax) { //joueur Rouge
                if (eval_move > bestRatio) {
                    bestRatio = eval_move;
                    bestMov = move;
                }
            } else {  //joueur Bleu
                if (eval_move < bestRatio) {
                    bestRatio = eval_move;
                    bestMov = move;
                }
            }
        }
    }
    printf("------------\nBEST RATIO : %d\n------------\n", bestRatio);

    return bestMov;
}

std::vector<MoveResult> Strategy::generate_moves(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, bool joueurMax) {
    std::vector<MoveResult> new_positions;

    movement mv(0, 0, 0, 0);
    int player = joueurMax ? 0: 1;
    for (mv.ox = 0; mv.ox < 8; mv.ox++) {
        for (mv.oy = 0; mv.oy < 8; mv.oy++) {
            if (plateau.get(mv.ox, mv.oy) == player) {
                // Iterate on possible destinations
                for (mv.nx = std::max(0, mv.ox - 2); mv.nx <= std::min(7, mv.ox + 2); mv.nx++) {
                    for (mv.ny = std::max(0, mv.oy - 2); mv.ny <= std::min(7, mv.oy + 2); mv.ny++) {
                        if (!_holes.get(mv.nx, mv.ny) && plateau.get(mv.nx, mv.ny) == -1) {
                            // mouvement valid
                            // appliquer le coup
                            int newNbRouge = nbRouge;
                            int newNbBleu = nbBleu;
                            bidiarray<Sint16> nouvellePos = applyMove(newNbRouge, newNbBleu, mv, plateau, joueurMax);
                            // Ajouter la nouvelle position et les nouvelles valeurs de newNbRouge et newNbBleu à la structure MoveResult
                            MoveResult result;
                            result.newPosition = nouvellePos;
                            result.newNbRouge = newNbRouge;
                            result.newNbBleu = newNbBleu;
                            // Ajouter la structure au vecteur de résultats
                            new_positions.push_back(result);
                        }
                    }
                }
            }
        }
    }

    return new_positions;
}





/******************************
* Test de miniMax en parallel
*******************************/

// int Strategy::minimaxPara(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax) {
//     nbNoeudsArbre ++;
//     if (profondeur == 0){
//         return eval_position(nbRouge, nbBleu);
//     }
//     int bestRatio = joueurMax ? -10000 : 10000;
//     std::mutex mtx;
//     auto new_positions = generate_moves(nbRouge, nbBleu, plateau, joueurMax);

//     if(new_positions.empty()){    //si il n'y a aucun mouvement possible
//         return eval_position(nbRouge, nbBleu);
//     }

//     ThreadPool pool(4); // Créez un pool de threads avec 4 threads par exemple


//     // Parcourir le vecteur new_positions pour accéder à chaque élément
//     for (const auto& result : new_positions) {
//         const auto& newPosition = result.newPosition;
//         int newNbRouge = result.newNbRouge;
//         int newNbBleu = result.newNbBleu;

//         // Ajouter la tâche à exécuter dans le pool de threads
//         pool.enqueue([&] {
//             int eval_child = minimaxPara(newNbRouge, newNbBleu, newPosition, profondeur - 1, !joueurMax);
//             std::lock_guard<std::mutex> lock(mtx);
//             if (joueurMax) { //joueur Rouge
//                 if (eval_child > bestRatio) {
//                     bestRatio = eval_child;
//                 }
//             } else {  //joueur Bleu
//                 if (eval_child < bestRatio) {
//                     bestRatio = eval_child;
//                 }
//             }
//         });
//     }

//     // Attendre la fin de toutes les tâches
//     std::this_thread::sleep_for(std::chrono::milliseconds(100)); // Ajoutez une attente pour permettre aux tâches de se terminer

//     return bestRatio;
// }


// movement Strategy::find_best_move_min_max(int nbRouge, int nbBleu, const bidiarray<Sint16>& plateau, int profondeur, bool joueurMax){
//     movement bestMov(0, 0, 0, 0);
//     int currentJoueur = joueurMax ? 0 : 1;
//     int bestRatio = joueurMax ? -10000 : 10000;
//     std::mutex mtx;
//     vector<movement> valid_moves;
//     computeValidMoves(valid_moves, currentJoueur, plateau);

//     ThreadPool pool(4); // Créez un pool de threads avec 4 threads par exemple

//     // Fonction exécutée par chaque thread
//     for (const auto& move : valid_moves) {
//         // Ajouter la tâche à exécuter dans le pool de threads
//         pool.enqueue([&] {
//             // appliquer le coup
//             int newNbRouge = nbRouge;
//             int newNbBleu = nbBleu;
//             bidiarray<Sint16> nouvellePos = applyMove(newNbRouge, newNbBleu, move, plateau, joueurMax);
            
//             int eval_move = minimaxPara(newNbRouge, newNbBleu, nouvellePos, profondeur, !joueurMax);
//             std::lock_guard<std::mutex> lock(mtx);
//             if (profondeur == 3){
//                 printf("Mouvement : %i %i , %i %i\n", move.ox, move.oy, move.nx, move.ny);
//             }
//             if (joueurMax) { //joueur Rouge
//                 if (eval_move > bestRatio) {
//                     bestRatio = eval_move;
//                     bestMov = move;
//                 }
//             } else {  //joueur Bleu
//                 if (eval_move < bestRatio) {
//                     bestRatio = eval_move;
//                     bestMov = move;
//                 }
//             }
//         });
//     }

//     // Attendre la fin de toutes les tâches
//     std::this_thread::sleep_for(std::chrono::milliseconds(900)); // Ajoutez une attente pour permettre aux tâches de se terminer
//     if (profondeur == 3){
//         printf("BEST RATIO = %i\n", bestRatio);
//     }
//     return bestMov;
// }
