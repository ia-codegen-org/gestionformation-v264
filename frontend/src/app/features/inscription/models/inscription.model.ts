// ─── Modèle généré automatiquement par IACodeGen

export interface Inscription {
  id?: number;
  statut: string;
  dateInscription: string;
  session?: { id: number };
  etudiant?: { id: number };
  paiement?: { id: number };
}

export interface InscriptionCreateDTO {
  statut: string;
  dateInscription: string;
  sessionId: number;
  etudiantId: number;
  paiementId: number;
}

export interface InscriptionUpdateDTO {
  statut?: string;
  dateInscription?: string;
  sessionId?: number;
  etudiantId?: number;
  paiementId?: number;
}

export interface InscriptionResponseDTO {
  id: number;
  statut: string;
  dateInscription: string;
  sessionId?: number;
  etudiantId?: number;
  paiementId?: number;
}
