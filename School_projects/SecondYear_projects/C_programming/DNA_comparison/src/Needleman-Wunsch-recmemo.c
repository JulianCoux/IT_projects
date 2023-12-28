/**
 * \file Needleman-Wunsch-recmemo.c
 * \brief recursive implementation with memoization of Needleman-Wunsch global alignment algorithm that computes the distance between two genetic sequences 
 * \version 0.1
 * \date 03/10/2022 
 * \author Jean-Louis Roch (Ensimag, Grenoble-INP - University Grenoble-Alpes) jean-louis.roch@grenoble-inp.fr
 *
 * Documentation: see Needleman-Wunsch-recmemo.h
 * Costs of basic base opertaions (SUBSTITUTION_COST, SUBSTITUTION_UNKNOWN_COST, INSERTION_COST) are
 * defined in Needleman-Wunsch-recmemo.h
 */


#include "Needleman-Wunsch-recmemo.h"
#include <stdio.h>  
#include <stdlib.h> 
#include <string.h> /* for strchr */
// #include <ctype.h> /* for toupper */
#include <math.h>
#include "characters_to_base.h" /* mapping from char to base */

/*****************************************************************************/
   
/* Context of the memoization : passed to all recursive calls */
/** \def NOT_YET_COMPUTED
 * \brief default value for memoization of minimal distance (defined as an impossible value for a distance, -1).
 */
#define NOT_YET_COMPUTED -1L 

/** \struct NW_MemoContext
 * \brief data for memoization of recursive Needleman-Wunsch algorithm 
*/
struct NW_MemoContext 
{
    char *X ; /*!< the longest genetic sequences */
    char *Y ; /*!< the shortest genetic sequences */
    size_t M; /*!< length of X */
    size_t N; /*!< length of Y,  N <= M */
    long **memo; /*!< memoization table to store memo[0..M][0..N] (including stopping conditions phi(M,j) and phi(i,N) */
} ;

/*
 *  static long EditDistance_NW_RecMemo(struct NW_MemoContext *c, size_t i, size_t j) 
 * \brief  EditDistance_NW_RecMemo :  Private (static)  recursive function with memoization \
 * direct implementation of Needleman-Wursch extended to manage FASTA sequences (cf TP description)
 * \param c : data passed for recursive calls that includes the memoization array 
 * \param i : starting position of the left sequence :  c->X[ i .. c->M ] 
 * \param j : starting position of the right sequence :  c->Y[ j .. c->N ] 
 */ 
static long EditDistance_NW_RecMemo(struct NW_MemoContext *c, size_t i, size_t j) 
/* compute and returns phi(i,j) using data in c -allocated and initialized by EditDistance_NW_Rec */
{
   if (c->memo[i][j] == NOT_YET_COMPUTED)
   {  
      long res ;
      char Xi = c->X[i] ;
      char Yj = c->Y[j] ;
      if (i == c->M) /* Reach end of X */
      {  if (j == c->N) res = 0;  /* Reach end of Y too */
         else res = (isBase(Yj) ? INSERTION_COST : 0) + EditDistance_NW_RecMemo(c, i, j+1) ;
      }
      else if (j == c->N) /* Reach end of Y but not end of X */
      {  res = (isBase(Xi) ? INSERTION_COST : 0) + EditDistance_NW_RecMemo(c, i+1, j) ;
      }
      else if (! isBase(Xi))  /* skip ccharacter in Xi that is not a base */
      {  ManageBaseError( Xi ) ;
         res = EditDistance_NW_RecMemo(c, i+1, j) ;
      }
      else if (! isBase(Yj))  /* skip ccharacter in Yj that is not a base */
      {  ManageBaseError( Yj ) ;
         res = EditDistance_NW_RecMemo(c, i, j+1) ;
      }
      else  
      {  /* Note that stopping conditions (i==M) and (j==N) are already stored in c->memo (cf EditDistance_NW_Rec) */ 
         long min = /* initialization  with cas 1*/
                   ( isUnknownBase(Xi) ?  SUBSTITUTION_UNKNOWN_COST 
                          : ( isSameBase(Xi, Yj) ? 0 : SUBSTITUTION_COST ) 
                   )
                   + EditDistance_NW_RecMemo(c, i+1, j+1) ; 
         { long cas2 = INSERTION_COST + EditDistance_NW_RecMemo(c, i+1, j) ;      
           if (cas2 < min) min = cas2 ;
         }
         { long cas3 = INSERTION_COST + EditDistance_NW_RecMemo(c, i, j+1) ;      
           if (cas3 < min) min = cas3 ; 
         }
         res = min ;
      }
       c->memo[i][j] = res ;
   }
   return c->memo[i][j] ;
}

/* EditDistance_NW_Rec :  is the main function to call, cf .h for specification 
 * It allocates and initailizes data (NW_MemoContext) for memoization and call the 
 * recursivefunction EditDistance_NW_RecMemo 
 * See .h file for documentation
 */
long EditDistance_NW_Rec(char* A, size_t lengthA, char* B, size_t lengthB)
{
   _init_base_match() ;
   struct NW_MemoContext ctx;
   if (lengthA >= lengthB) /* X is the longest sequence, Y the shortest */
   {  ctx.X = A ;
      ctx.M = lengthA ;
      ctx.Y = B ;
      ctx.N = lengthB ;
   }
   else
   {  ctx.X = B ;
      ctx.M = lengthB ;
      ctx.Y = A ;
      ctx.N = lengthA ;
   }
   size_t M = ctx.M ;
   size_t N = ctx.N ;
   {  /* Allocation and initialization of ctx.memo to NOT_YET_COMPUTED*/
      /* Note: memo is of size (N+1)*(M+1) but is stored as (M+1) distinct arrays each with (N+1) continuous elements 
       * It would have been possible to allocate only one big array memezone of (M+1)*(N+1) elements 
       * and then memo as an array of (M+1) pointers, the memo[i] being the address of memzone[i*(N+1)].
       */ 
      ctx.memo = (long **) malloc ( (M+1) * sizeof(long *)) ;
      if (ctx.memo == NULL) { perror("EditDistance_NW_Rec: malloc of ctx_memo" ); exit(EXIT_FAILURE); }
      for (int i=0; i <= M; ++i) 
      {  ctx.memo[i] = (long*) malloc( (N+1) * sizeof(long));
         if (ctx.memo[i] == NULL) { perror("EditDistance_NW_Rec: malloc of ctx_memo[i]" ); exit(EXIT_FAILURE); }
         for (int j=0; j<=N; ++j) ctx.memo[i][j] = NOT_YET_COMPUTED ;
      }
   }    
   
   /* Compute phi(0,0) = ctx.memo[0][0] by calling the recursive function EditDistance_NW_RecMemo */
   long res = EditDistance_NW_RecMemo( &ctx, 0, 0 ) ;
    
   { /* Deallocation of ctx.memo */
      for (int i=0; i <= M; ++i) free( ctx.memo[i] ) ;
      free( ctx.memo ) ;
   }
   return res ;
}


long mini(long a, long b) {
    return (a < b) ? a : b;
}


long Distance_Ite(char* X, size_t lengthX, char* Y, size_t lengthY){
   _init_base_match();
   long min;
   long max;
   if (lengthX >= lengthY){ // Y est le min
      min = lengthY;
      max = lengthX;
   } 
   else { // X est le min mais Y le devient
      min = lengthX;
      max = lengthY; 
      char* new_X = Y;
      Y = X;
      X = new_X;
   }

   //Initialisation de 2 liste à 0 de taille min + 1
   long* liste1 = (long*)malloc((min + 1) * sizeof(long));
   long* liste2 = (long*)malloc((min + 1) * sizeof(long));
   memset(liste2, 0, (min + 1) * sizeof(long));
   liste1[0] = 0;
   liste2[0] = 2 * (isBase(X[0]));
   for(int i = 1; i < (min + 1); i++){
      liste1[i] = 2*(isBase(Y[i - 1])) + liste1[i - 1];
   }

   //On stock l'adresse de la première liste
   long* adresse_liste1;

   //On parcourt la 2ème liste m fois
   for (int i = 1; i < (max + 1); i++){
      //On parcourt chaque élé de la deuxième liste de 1 à n + 1
      char cara_X = X[i - 1];
      for (int j = 1; j < (min + 1); j++){
         char cara_Y = Y[j - 1];
         //On met tout en majuscule
         if (isBase(cara_X) == 0 && isBase(cara_Y) == 0){
            liste2[j] = mini(liste1[j], liste2[j - 1]);
         } else if (isBase(cara_X) == 0){
            liste2[j] = liste1[j];
         } else if (isBase(cara_Y) == 0){
            liste2[j] = liste2[j - 1];
         } else { //caractère diff
            if ('a' <= cara_X && cara_X <= 'z') {
               cara_X = cara_X - 32;
            }
            if ('a' <= cara_Y && cara_Y <= 'z') {
               cara_Y = cara_Y - 32;
            }
            int diff = cara_X == cara_Y ? 0: 1;
            liste2[j] = mini(mini(liste2[j - 1] + 2, liste1[j] + 2), liste1[j - 1] + diff);
         }
         // if (cara_X == cara_Y){ //caractère égaux
         //    liste2[j] = liste1[j-1];

      }
      //On fait pointer la liste 1 vers la liste 2 pour la stocker et la liste 2 vers la liste 1 pour la modifier
      //On change bien l'adresse liste 1 vers sa nouvelle adresse
      adresse_liste1 = liste1;
      liste1 = liste2;
      liste2 = adresse_liste1;
      liste2[0] = 2 * (isBase(X[i - 1])) + liste1[0];
   }
   return liste1[min];
}


int difference(char cara_X, char cara_Y){
   if ('a' <= cara_X && cara_X <= 'z') {
      cara_X = cara_X - 32;
   }
   if ('a' <= cara_Y && cara_Y <= 'z') {
      cara_Y = cara_Y - 32;
   }
   int diff = cara_X == cara_Y ? 0: 1;
   return diff;
}

long cacheAware(char* X, size_t lengthX, char* Y, size_t lengthY, long Z){
   //On met le bon truc sur les lignes
   _init_base_match() ;
   long min;
   long max;
   if (lengthX >= lengthY){ // Y est le min
      min = lengthY;
      max = lengthX;
   } 
   else { // X est le min mais Y le devient
      min = lengthX;
      max = lengthY; 
      char* new_X = Y;
      Y = X;
      X = new_X;
   }
   long K;
   if (min >= 1/4 + Z){
      K = Z;
   } else {
      K = (-1 + sqrt(1 - 4 * (min - Z))) / 2;
   }
   long der = 0;
   long res = 0;
   //On alloue tout ce dont on a besoin
   // long* liste1 = (long*)malloc(K * sizeof(long));
   long* liste1 = calloc(K+1, sizeof(long));
   long* adresse_liste1;
   // long* liste2 = (long*)malloc(K * sizeof(long));
   // long* colonneK = (long*)malloc(K * sizeof(long));
   // long** listeM = (long **)malloc(((min + 1) / K + 1) * sizeof(long *));
   long* liste2 = calloc(K, sizeof(long));
   long* colonneK = calloc(K, sizeof(long));
   long** listeM = calloc((min + 1) / K + 1, sizeof(long*));
   for (long i = 0; i < ((min + 1) / K + 1); i++) {
      listeM[i] = calloc(K, sizeof(long));
   }
   //Parcourt de toute la matrice
   for (long I = 0; I < (max + 1); I += K){ //Boucle sur les lignes de la matrice
      long i_end = mini(I + K, (max + 1));
      for (long J = 0; J < (min + 1); J += K){ //Boucle sur les colonnes de la matrice
         long j_end = mini(J + K, (min + 1));
         //On va initialiser la liste1 en fonction de oÃ¹ on est
         if (I == 0){ //Cas de la premiÃ¨re ligne de bloc
            if (J == 0){
               liste1[0] = 0;
            } else {
               liste1[0] = 2*(isBase(Y[J - 1])) + colonneK[0];
            }
            for (long index = 1; index < (j_end - J); index++){ //Initialisation Liste1
               liste1[index] = 2*(isBase(Y[J + (index - 1)])) + liste1[index - 1];
            }
         } else { //Cas sur les lignes diffÃ©rentes de la premiÃ¨re
            char cara_X_prec = X[I - 1];
            if (J == 0){ //premier bloc d'une nouvelle ligne
               liste1[0] = 2*(isBase(X[I - 1])) + listeM[0][0];
               for (long index = 1; index < (j_end - J); index++){
                  char cara_Y_cour = Y[J + index - 1];
                  if (isBase(cara_X_prec) == 0 && isBase(cara_Y_cour) == 0){
                     liste1[index] = mini(listeM[0][index], liste1[index - 1]);
                  } else if (isBase(cara_X_prec) == 0){
                     liste1[index] = listeM[0][index];
                  } else if (isBase(cara_Y_cour) == 0){
                     liste1[index] = liste1[index - 1];
                  } else {
                     int diff = difference(cara_X_prec, cara_Y_cour);
                     liste1[index] = mini(mini(liste1[index - 1] + 2, listeM[0][index] + 2), listeM[0][index - 1] + diff);
                  }
               }
            } else { //Bloc quelquonnque
               for (long index = 0; index < (j_end - J); index++){
                  char cara_Y_cour = Y[J + index - 1];
                  if (isBase(cara_X_prec) == 0 && isBase(cara_Y_cour) == 0){
                     if (index == 0){
                        liste1[index] = mini(listeM[J/K][index], colonneK[0]);
                     } else {
                        liste1[index] = mini(listeM[J/K][index], liste1[index - 1]);
                     }
                  } else if (isBase(cara_X_prec) == 0){
                     liste1[index] = listeM[J/K][index];
                  } else if (isBase(cara_Y_cour) == 0){
                     if (index == 0) {
                        liste1[index] = colonneK[0];
                     } else {
                        liste1[index] = liste1[index - 1];
                     }
                  } else {
                     int diff = difference(cara_X_prec, cara_Y_cour);
                     if (index == 0){
                        liste1[0] = mini(mini(colonneK[0] + 2, listeM[J / K][0] + 2), der + diff);
                     } else {
                        liste1[index] = mini(mini(liste1[index - 1] + 2, listeM[J / K][index] + 2), listeM[J / K][index - 1] + diff);
                     }
                  }
               }
            }
         }
         if (i_end - I == 0){ // Si une seule ligne Ã  remplir A FACTORISER
            //On attribue la derniÃ¨re valeur de la colonne
            colonneK[(i_end - I) - 1] = liste1[(j_end - J) - 1];
            //On stock le dernier Ã©lÃ©ment de listeM[J/K] avant de modifier la liste car on en aura besoin pour le premier Ã©lÃ©ment du bloc s'il est au milieu
            if (I != 0){
               der = listeM[J / K][j_end - J - 1];
            }
            if (I + K >= (max + 1) && J + K >= (min + 1)){
               res = liste1[j_end - J - 1];
            }
         }
         //Parcourt des blocs KxK
         for (long i = 1; i < (i_end - I); ++i){ //Boucle sur les lignes du bloc KxK Ã  partir de la premiÃ¨re
            char cara_X = X[I + (i - 1)];
            if ((j_end - J) == 1){ // Cas oÃ¹ il n'y a qu'une  colonne Ã  remplir
               //init liste2[0] factoriser si Ã§a marche mais mÃªme problÃ¨me avec i du coup
               if (J == 0){ //premiÃ¨re colonne de la matrice
                  liste2[0] = 2*(isBase(X[I + i])) + liste1[0]; //Je viens de changer
               } else {  //premiÃ¨re valeur d'un bloc quelquonque
                  char cara_Y_prec = Y[J - 1];
                  if (isBase(cara_X) == 0 && isBase(cara_Y_prec) == 0){
                     liste2[0] = mini(liste1[0], colonneK[1]);
                  } else if (isBase(cara_X) == 0){
                     liste2[0] = liste1[0];
                  } else if (isBase(cara_Y_prec) == 0){
                     liste2[0] = colonneK[1];
                  } else { 
                     int diff = difference(cara_X, cara_Y_prec);
                     liste2[0] = mini(mini(colonneK[i] + 2, liste1[0] + 2), colonneK[i - 1] + diff);
                  }
               }
            }
            for (long j = 1; j < (j_end - J); j++){ //Boucle sur les colonnes du bloc KxK Ã  partir de la premiÃ¨re
               //Initialiser liste2[0]
               if (J == 0){ //premiÃ¨re colonne de la matrice
                  liste2[0] = 2*(isBase(X[I + i])) + liste1[0];
               } else if (j == 1 && J != 0){  //premiÃ¨re valeur d'un bloc quelquonque
                  char cara_Y_prec = Y[J - 1];
                  if (isBase(cara_X) == 0 && isBase(cara_Y_prec) == 0){
                     liste2[0] = mini(liste1[0], colonneK[1]);
                  } else if (isBase(cara_X) == 0){
                     liste2[0] = liste1[0];
                  } else if (isBase(cara_Y_prec) == 0){
                     liste2[0] = colonneK[1];
                  } else {
                     int diff = difference(cara_X, cara_Y_prec);
                     liste2[0] = mini(mini(colonneK[i] + 2, liste1[j - 1] + 2), colonneK[i - 1] + diff);
                  }
               }
               char cara_Y = Y[J + (j - 1)];
               //ALGO ITE
               if (isBase(cara_X) == 0 && isBase(cara_Y) == 0){
                  liste2[j] = mini(liste1[j], liste2[j - 1]);
               } else if (isBase(cara_X) == 0){
                  liste2[j] = liste1[j];
               } else if (isBase(cara_Y) == 0){
                  liste2[j] = liste2[j - 1];
               } else {
                  int diff = difference(cara_X, cara_Y);
                  liste2[j] = mini(mini(liste2[j - 1] + 2, liste1[j] + 2), liste1[j - 1] + diff);
               }
            }
            //On attribue Ã  la liste colonneK la derniÃ¨re valeur de la liste1 afin de la stocker pour le prochain bloc
            colonneK[i - 1] = liste1[(j_end - J) - 1];
            
            //On fait pointer la liste 1 vers la liste 2 pour la stocker et la liste 2 vers la liste 1 pour la modifier
            //On change bien l'adresse liste 1 vers sa nouvelle adresse
            adresse_liste1 = liste1;
            liste1 = liste2;
            liste2 = adresse_liste1;
         }
         //On attribue la derniÃ¨re valeur de la colonne
         colonneK[(i_end - I) - 1] = liste1[(j_end - J) - 1];
         //On stock le dernier Ã©lÃ©ment de listeM[J/K] avant de modifier la liste car on en aura besoin pour le premier Ã©lÃ©ment du bloc s'il est au milieu
         if (I != 0){
            der = listeM[J / K][j_end - J - 1];
         }
         //On stock la ligne pour les blocs qui seront en dessous
         for (int i = 0; i < j_end-J; i++){
            listeM[J/K][i]=liste1[i];  
         }
         if (I + K >= (max + 1) && J + K >= (min + 1)){
            res = liste1[j_end - J - 1];
         }
      } 
   }
   return res;
}
