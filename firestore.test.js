const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user cannot read students", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("students").get());
});

test("Authenticated user cannot read another user's student", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("students").doc("bob_student").set({
      userId: BOB_UID,
      enrollmentNo: "ENR101",
      fullName: "Bob Student",
      gradeClass: "Class 10",
      batch: "Morning",
      status: "ACTIVE",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("students").doc("bob_student").get());
});

test("Authenticated user can query their own students", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("students").doc("alice_student").set({
      userId: ALICE_UID,
      enrollmentNo: "ENR102",
      fullName: "Alice Student",
      gradeClass: "Class 12",
      batch: "Evening",
      status: "ACTIVE",
      createdAt: new Date(),
    });
  });

  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("students").where("userId", "==", ALICE_UID).get()
  );
});

test("Authenticated user cannot query students without userId filter (rules are not filters)", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("students").get());
});

test("Authenticated user can create a valid notice", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertSucceeds(
    aliceDb.collection("notices").doc("notice_1").set({
      userId: ALICE_UID,
      title: "Mid-Term Examination Schedule",
      content: "All classes will undergo mid-term examinations next week.",
      category: "EXAM",
      targetAudience: "ALL",
      createdAt: new Date(),
    })
  );
});

test("Authenticated user cannot create notice for another userId", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("notices").doc("notice_bad").set({
      userId: BOB_UID,
      title: "Spoofed Notice",
      content: "Invalid spoofed content",
      category: "GENERAL",
      targetAudience: "ALL",
      createdAt: new Date(),
    })
  );
});
