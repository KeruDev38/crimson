export type AccountDetails = {
  accountId: string;
  customerId: string;
  customerFirstName: string;
  customerLastName: string;
  customerEmail: string;
  currency: string;
  balance: number;
  status: string;
  createdAt: string;
  updatedAt: string;
};

export type TransactionItem = {
  transactionId: string;
  senderAccountId: string;
  receiverAccountId: string;
  amount: number;
  currency: string;
  status: string;
  reference: string;
  createdAt: string;
};

export type PageResponse<T> = {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
};

export type CreateAccountPayload = {
  customerId: string;
  currency: string;
  initialBalance: number;
};

export type CreateCustomerPayload = {
  firstName: string;
  lastName: string;
  email: string;
};

export type CustomerItem = {
  customerId: string;
  firstName: string;
  lastName: string;
  email: string;
  createdAt: string;
};

export type TransferPayload = {
  senderAccountId: string;
  receiverAccountId: string;
  amount: number;
  currency: string;
  reference: string;
};
