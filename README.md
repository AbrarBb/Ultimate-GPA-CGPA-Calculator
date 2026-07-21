# Ultimate GPA/CGPA Calculator
## GPA/CGPA Calculator for Bangladesh

---

## 1. VISION & POSITIONING

Build the most complete, offline-first academic companion app for Bangladeshi
students — covering SSC, HSC, National University, and Private/Public
University systems in one app, with genuinely differentiated features
(admission eligibility checker, combined SSC+HSC average, target CGPA
planning) that no existing calculator app bundles together.

**Positioning statement (for Play Store listing):**
> "The only GPA/CGPA calculator built for Bangladesh — from SSC to
> university, with admission eligibility checking, custom grading scales,
> and full result history saved offline."

**Primary goal:** Become the #1 academic calculator app for Bangladeshi
students, by being the fastest, most trustworthy, most locally-accurate tool
in this category — not by having the most features on day one.

---

## 2. TARGET AUDIENCE

- SSC students (Science / Commerce / Arts)
- HSC students (Science / Commerce / Arts)
- National University students (Honours / Degree / Masters)
- Public University students (DU, BUET, RUET, KUET, CUET, SUST, etc.)
- Private University students (EWU, NSU, BRAC, AIUB, DIU, UIU, IUB, etc.)
- Guardians tracking a child's academic progress
- Tutors / coaching centers tracking multiple students

---

## 3. TECH STACK

| Layer | Choice |
|---|---|
| Language | Java |
| Architecture | MVVM |
| Database | Room (SQLite) |
| Min SDK | Android 8.0 (API 26) |
| Target SDK | Latest stable |
| UI | Material Design 3, XML layouts |
| Dark Mode | Yes, full support |
| Offline | 100% — no backend required for core features |
| Ads | AdMob (banner, interstitial, rewarded) |
| Analytics | Firebase Analytics (screen/action events only — never grade data) |
| Crash Reporting | Firebase Crashlytics |
| Notifications | WorkManager (result-day reminders, custom reminders) |
| Charts | MPAndroidChart |
| PDF Export | Android PDFDocument API |
| Image Export | Canvas |
| Localization | English + Bangla (core screens in v1, full app later) |

---

## 4. VERIFIED GRADING RULES (web-search confirmed, current as of 2026)

### 4.1 SSC / HSC (School mode) — GPA scale out of 5.00
| Marks | Grade | Point |
|---|---|---|
| 80–100 | A+ | 5.00 |
| 70–79 | A | 4.00 |
| 60–69 | A- | 3.50 |
| 50–59 | B | 3.00 |
| 40–49 | C | 2.00 |
| 33–39 | D | 1.00 |
| 0–32 | F | 0.00 |

- GPA without 4th subject = average of grade points of compulsory subjects only.
- GPA with 4th subject = GPA without 4th subject + max(0, fourthSubjectPoint − 2.00), capped at 5.00.
- SSC/HSC do **not** have a "CGPA" concept — each is a standalone exam result. CGPA has been discussed for secondary education but is not yet active.
- Note: this scale is consistent across General Education Boards and Dakhil/Madrasa boards.

### 4.2 National University (Honours/Degree/Masters) — verified official table
| Marks | Grade | Point |
|---|---|---|
| 80–100 | A+ | 4.00 |
| 75–79 | A | 3.75 |
| 70–74 | A- | 3.50 |
| 65–69 | B+ | 3.25 |
| 60–64 | B | 3.00 |
| 55–59 | B- | 2.75 |
| 50–54 | C+ | 2.50 |
| 45–49 | C | 2.25 |
| 40–44 | D | 2.00 |
| 0–39 | F | 0.00 |

- Pass mark: 40% overall (in-course 8/20 + written 32/80).
- **Year-based** calculation (1st–4th Year for Honours), not semester-based.
- Class: 3.00–4.00 = 1st class, 2.25–2.99 = 2nd class, 2.00–2.249 = 3rd class.
- Retake: replace old grade with new (REPLACE rule).
- Marks entry: **supported** (official table exists).

### 4.3 BRAC University — verified official table (bracu.ac.bd, effective Fall 2020)
| Marks | Grade | Point |
|---|---|---|
| 97–100 | A+ | 4.00 |
| 90–<97 | A | 4.00 |
| 85–<90 | A- | 3.70 |
| 80–<85 | B+ | 3.30 |
| 75–<80 | B | 3.00 |
| 70–<75 | B- | 2.70 |
| 65–<70 | C+ | 2.30 |
| 60–<65 | C | 2.00 |
| 57–<60 | C- | 1.70 |
| 55–<57 | D+ | 1.30 |
| 52–<55 | D | 1.00 |
| 50–<52 | D- | 0.70 |
| <50 | F | 0.00 |

- Retake: REPLACE — old grade fully replaced by new.
- Good standing minimum CGPA: 2.00 (dismissal review after 3 consecutive semesters below, or 2 for Pharmacy).
- Marks entry: **supported** (official table exists).

### 4.4 EWU (East West University)
- Scale: 0–4.00, standard UGC-style letters (A+ through F, D is lowest passing).
- Only courses graded A+, A, A-, B+, B, B-, C+, C, D, F count toward attempted credits.
- Retake: **REPLACE** — GPA/CGPA calculated using only the last attempt.
- No officially-published marks-to-grade table found — **letter-grade entry only**, do not fabricate a marks table.

### 4.5 NSU (North South University)
- Scale: 0–4.00, letters A, A-, B+, B, B-, C+, C, C-, D+, D, F (+/- = 0.3 point increments).
- Retake: **conditional REPLACE** — only courses graded B or lower are retake-eligible; F grades specifically stay counted in CGPA until the course is retaken **and** the student formally applies to have the F excluded.
- Class: 3.00+ = First Class, 2.50–2.99 = Second Class, 2.00–2.49 = Third Class.
- No officially-published marks-to-grade table confirmed from NSU's own site — **letter-grade entry only**; marks ranges from third-party sites should not be trusted as official.

### 4.6 Generic/Other UGC-style Public University fallback
- Same 4.00 scale as NU (A+ 4.00 → F 0.00), used as an editable default for any public university not explicitly modeled (DU, BUET, RUET, KUET, CUET, SUST, etc.) until that institution's specific rules are confirmed and added.

**Golden rule for all institution data:** grading scales and retake rules are **user-editable data, never hardcoded permanently** — universities revise policy, and a wrong hardcoded rule that can't be corrected in-app is worse than no rule at all.

---

## 5. CORE MODES

### Mode A — School (SSC/HSC)
- Subject-wise entry (marks or direct letter grade)
- Compulsory + optional (4th) subject clearly separated
- Group presets: Science / Commerce / Arts auto-load correct compulsory subject lists
  - Science: Physics, Chemistry, Biology, Higher Math, ICT, Bangla, English, Religion, Bangladesh Studies
  - Commerce / Arts: auto-loaded group-specific sets
- Outputs: GPA without 4th subject, GPA with 4th subject (shown together)
- SSC and HSC saved as two independent exam records (not "semesters")
- **Combined SSC+HSC average** — differentiator feature for admission formulas
- Edit/delete past results
- PDF export, share

### Mode B — University (Semester or Year-based, per institution)
- Institution picker: EWU, NSU, BRAC, National University, DU/BUET/other public (generic UGC), Custom
- Institution preset auto-loads: grading scale, retake rule, marks-entry support flag, minimum passing CGPA
- Semester GPA (credit-weighted) or Year GPA (National University mode)
- Cumulative CGPA across all saved semesters/years
- Retake handling per institution's actual rule (REPLACE / conditional REPLACE / AVERAGE if user's institution uses that)
- Marks-entry toggle — only shown for institutions with a verified table (NU, BRAC); others get letter-grade-only entry
- Semester-wise / year-wise breakdown + CGPA trend chart

### Shared across both modes
- Custom grading scale builder (for O-Level/A-Level, unlisted boards, or institutions with revised policy)
- Full local persistence (Room DB), edit history retained
- Backup/export to JSON, restore on reinstall
- Transcript-style PDF export + image export + share
- Dark mode
- Bangla + English localization (core screens first)
- Multi-profile support (guardians/tutors tracking multiple students)

---

## 6. DIFFERENTIATING / GAP-FILLING FEATURES

1. **Admission Eligibility Checker** — enter GPA/CGPA, see which universities/programs the student qualifies for. Cutoff data stored as an editable, updatable local dataset (not hardcoded), since requirements change yearly.
2. **Admission Score Calculator** — user-defined weighted formula (e.g. SSC×5 + HSC×5 + written test score).
3. **Scholarship / Dean's List Checker** — reuses the same generic threshold-matching engine as #1, fed a different dataset. Don't build three separate features for what is structurally one comparison engine.
4. **Target CGPA Calculator** — given current CGPA + completed credits + desired CGPA + remaining credits, compute required GPA.
5. **Scenario Calculator** ("What-if" + "Pass mark calculator" merged into one module with tabs):
   - What if I get grade X in a course?
   - What if I retake a course?
   - What if next semester's GPA is Y?
   - What do I need in the final exam to pass / hit a target grade, given midterm/quiz/assignment marks so far?
6. **Result publication reminders** — SSC/HSC result-day notifications, plus user-set semester result/registration/assignment reminders (WorkManager).
7. **Multi-profile support** — one device, multiple students; useful for guardians and tutoring centers.
8. **Degree progress bar** — credits completed / remaining / expected graduation estimate.
9. **Statistics dashboard** — highest/lowest/average GPA, retake count, trend graph, semester-by-semester chart.
10. **Transcript-style PDF export** — formatted to look submission-worthy (student info, institution, semester/year breakdown, GPA/CGPA, optional QR code + signature line), not a plain text dump.
11. **Home screen widget** (v2) — current CGPA/GPA at a glance.

---

## 7. DATA MODEL (Room / Java)

```
Profile (id, name, activeMode)

GradingScale (id, name, mode[SCHOOL|UNIVERSITY], isCustom)
  └─ GradePoint (id, scaleId, letterGrade, pointValue, minMarks, maxMarks)
       // minMarks/maxMarks = -1 when no verified marks table exists

InstitutionPreset (id, name, defaultScaleId, calcMode[SEMESTER|YEAR],
                    retakeRule[REPLACE|REPLACE_CONDITIONAL|AVERAGE],
                    retakeEligibleGrades, retakeRequiresApprovalForF,
                    minPassingCgpaOrGpa, supportsMarksEntry, isEditable)

ExamRecord (id, examType[SSC|HSC], group[SCIENCE|COMMERCE|ARTS], scaleId, profileId)
  └─ Subject (id, examRecordId, subjectName, letterGrade, isOptionalFourth)

Semester (id, name, scaleId, profileId, sortOrder)
  └─ Course (id, semesterId, courseName, creditHours, letterGrade,
             isRetake, originalCourseId, rawMarks)

AdmissionCutoff (id, universityName, programName, minGpaRequired, formulaNote)
```

---

## 8. CORE CALCULATION LOGIC

**School GPA:**
```
GPA_without_4th = Σ(gradePoint of compulsory subjects) / count(compulsory subjects)
bonus = max(0, fourthSubjectGradePoint - 2.00)
GPA_with_4th = min(5.00, GPA_without_4th + bonus)
Combined_SSC_HSC_avg = (SSC_GPA_with_4th + HSC_GPA_with_4th) / 2
```

**University semester/year GPA:**
```
GPA = Σ(gradePoint_i × creditHours_i) / Σ(creditHours_i)
```

**University CGPA (retake-aware):**
```
1. Resolve retakes per institution rule:
   - REPLACE: keep only the highest-id (most recent) attempt per course group
   - REPLACE_CONDITIONAL: same, but only if original grade is in
     retakeEligibleGrades; if retakeRequiresApprovalForF and grade was F,
     keep the F counted until an explicit "approved" flag is set
   - AVERAGE: average grade points across all attempts into one synthetic entry
2. CGPA = Σ(gradePoint_i × creditHours_i across effective courses) / Σ(creditHours_i)
```

**Target/reverse calculator:**
```
requiredGPA = (targetCGPA × (currentCredits + remainingCredits) − currentTotalPoints) / remainingCredits
```

**Marks-to-grade derivation:**
```
Only call this for institutions where supportsMarksEntry == true (NU, BRAC).
For all others, require direct letter-grade selection — do not fabricate a table.
```

---

## 9. AD STRATEGY (AdMob)

**Principle: ads never interrupt data entry, calculation, or delay seeing a result.**

- **Banner:** bottom of Home Dashboard, History, Admission Checker, Statistics, Settings. Never on entry/calculator screens, dialogs, or PDF preview.
- **Interstitial:** only after a meaningful save action — saving an SSC/HSC result, saving a semester, restoring a backup, exporting a transcript (max once per few hours). Cap frequency to roughly one interstitial per 3–5 minutes of active use.
- **Rewarded:** unlocks PDF export, HD image export, unlimited custom scales, detailed admission eligibility results, advanced what-if simulations.
- **Native:** blended into History screen cards and near the bottom of the Admission section only — never inside form fields or calculators.
- **Premium (future):** one-time purchase or subscription to remove all ads permanently.

---

## 10. PROJECT FOLDER STRUCTURE

```
app/src/main/java/com/khatibstudio/gpacalc/
 ├── ui/
 │    ├── splash/
 │    ├── onboarding/
 │    ├── dashboard/
 │    ├── school/
 │    ├── university/
 │    ├── admission/
 │    ├── statistics/
 │    ├── history/
 │    ├── settings/
 │    └── profile/
 ├── data/
 │    ├── entity/
 │    ├── dao/
 │    └── AppDatabase.java, DefaultScaleSeeder.java
 ├── logic/          (SchoolGpaCalculator, UniversityGpaCalculator,
 │                     AdmissionEligibilityChecker)
 ├── viewmodel/
 ├── repository/
 ├── export/         (PDF, image, JSON backup/restore)
 ├── charts/
 ├── notification/   (WorkManager reminders)
 ├── ads/
 ├── widgets/        (v2)
 └── localization/
```

---

## 11. UI / DESIGN SYSTEM

- Style: Material 3, rounded corners, soft shadows, generous white space, minimal/premium feel — not a "typical calculator app" look
- Colors: Primary `#2563EB`, Secondary `#0EA5E9`, Success `#10B981`, Warning `#F59E0B`, Danger `#EF4444`, Background `#F8FAFC`, Dark `#111827`
- Typography: Inter or Google Sans, large titles, rounded buttons
- Dashboard: greeting + current CGPA with progress bar + credits completed/remaining, quick actions (Add Semester / Add SSC / Add HSC / Admission / Calculator), statistics summary, recent activity feed
- Navigation: bottom nav (Home, Calculator, Admission, History, Settings) + center FAB for quick-add (Semester / SSC / HSC / Profile)
- Animations: splash, card expansion, counter/progress animation on GPA reveal — kept subtle, not gimmicky

---

## 12. PHASED ROADMAP (realistic solo-dev pacing)

### Phase 1 — MVP (4–6 weeks)
- Splash, onboarding, single profile creation
- School GPA (SSC/HSC) with/without 4th subject, combined average
- University GPA/CGPA — EWU, NSU, BRAC, National University, generic UGC presets
- Save/edit/delete, Room persistence
- Target CGPA calculator
- Material 3 UI, dark mode
- AdMob banner + capped interstitial
- Bangla localization for core screens only

### Phase 2
- Multi-profile support
- Admission eligibility checker + scholarship/dean's-list checker (shared threshold engine)
- Scenario calculator (what-if + pass-mark merged module)
- Statistics dashboard + trend charts
- Result/notification reminders (WorkManager)

### Phase 3
- PDF/image transcript export
- JSON backup/restore
- Full-app Bangla localization
- Home screen widget

### Phase 4 (validate demand before building)
- Cloud sync
- OCR transcript import
- AI-powered study/GPA-improvement suggestions
- Premium subscription (ad removal, unlimited profiles/exports)

---

## 13. KEY PRINCIPLES TO ENFORCE THROUGHOUT

1. Grade data **never leaves the device** unless the user explicitly exports/shares it — market this as a trust feature.
2. Institution grading rules and admission cutoffs are **editable local datasets**, not hardcoded constants — they will need updates without app-store releases.
3. Never fabricate a marks-to-grade table for an institution without a verified official source (EWU, NSU currently excluded from marks-entry for this reason).
4. Ads must never sit between the user and seeing their calculated result.
5. Ship Phase 1 narrow and solid rather than wide and half-finished — validate before building National University, widgets, OCR, or AI features.
