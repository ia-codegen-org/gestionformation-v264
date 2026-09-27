// ─── Modèle généré automatiquement par IACodeGen

export interface Evaluation {
  id?: number;
  note: number;
  commentaire: string;
  session?: { id: number };
  etudiant?: { id: number };
}

export interface EvaluationCreateDTO {
  note: number;
  commentaire: string;
  sessionId: number;
  etudiantId: number;
}

export interface EvaluationUpdateDTO {
  note?: number;
  commentaire?: string;
  sessionId?: number;
  etudiantId?: number;
}

export interface EvaluationResponseDTO {
  id: number;
  note: number;
  commentaire: string;
  sessionId?: number;
  etudiantId?: number;
}
