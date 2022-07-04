import { IUser } from 'app/entities/user/user.model';

export interface IAddress {
  id?: number;
  name?: string;
  city?: string;
  district?: string;
  details?: string;
  user?: IUser | null;
}

export class Address implements IAddress {
  constructor(
    public id?: number,
    public name?: string,
    public city?: string,
    public district?: string,
    public details?: string,
    public user?: IUser | null
  ) {}
}

export function getAddressIdentifier(address: IAddress): number | undefined {
  return address.id;
}
